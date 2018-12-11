package org.jspace.io.binary;

import org.jspace.io.binary.exceptions.ParseException;
import org.jspace.io.binary.utilities.ClassRegistry;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import static org.jspace.io.binary.BinarySerializer.*;

public class Deserialization {
    private static HashSet<Byte> binaryPrimitives = initializeBinaryPrimitives(); // Used for checking if byte is a Primitive TAG.
    private static HashSet<Byte> initializeBinaryPrimitives() {
        HashSet<Byte> set = new HashSet<>();
        set.add(BOOL);
        set.add(INT);
        set.add(DOUBLE);
        set.add(CHAR);
        set.add(STRING);
        set.add(ENUM);
        return set;
    }

    private static HashMap<Class, PrimitiveTypes> primitives = new HashMap<>();
    private static HashMap<Class, PrimitiveTypes> initializePrimitives(){
        HashMap<Class, PrimitiveTypes> map = new HashMap<>();
        map.put(Boolean.class, PrimitiveTypes.Boolean);
        map.put(Byte.class, PrimitiveTypes.Byte);
        map.put(Double.class, PrimitiveTypes.Double);
        map.put(Float.class, PrimitiveTypes.Float);
        map.put(Short.class, PrimitiveTypes.Short);
        map.put(Integer.class, PrimitiveTypes.Integer);
        map.put(Long.class, PrimitiveTypes.Long);
        map.put(Character.class, PrimitiveTypes.Char);
        map.put(String.class, PrimitiveTypes.String);
        return map;
    }

    Object newObjectDeserialization(Class expectedType, InputStream stream, Configurations config) throws IOException, ParseException {
        int lastRead = stream.read(); // New TAG.
        if (lastRead == CLASS){
            expectedType = readClass(expectedType, stream, config);
            lastRead = stream.read();
            if (lastRead == CLASS)
                return expectedType;
        }
        if (binaryPrimitives.contains((byte)lastRead))
            return readPrimitive(expectedType, stream, config);
        else if (lastRead == ARRAY)
            return readArray(expectedType, stream, config);
        else if (lastRead == COLLECTION)
            return readCollection(expectedType, stream, config);
        else if (lastRead == OBJECT)
            return readObject(expectedType, stream, config);
        else
            return new Exception("Error. Invalid stream.");
    }

    private Class readClass(Class expectedType, InputStream stream, Configurations config) throws IOException {
        Class result = null;
        String className = TypeConverter.toString(read(stream, config), config.charEncoding);
        if (ClassRegistry.containsString(className))
            result = ClassRegistry.get(className);
        if (result == null)
            try {
                result = Class.forName(className);
            } catch (ClassNotFoundException e){
                // Do nothing.
            }
        if (result == null)
            result = expectedType; // Nothing else works, try and parse data into the expected type given.
        return result;
    }

    private Object readArray(Class expectedType, InputStream stream, Configurations config) throws IOException, ParseException {
        LinkedList<Integer> arrayLengths = new LinkedList<>();
        while (stream.read() == ARRAYLENGTH)
            arrayLengths.addLast(config.lengthConfig.toLength(stream)); // Array length

        int length = 0;
        for (Integer i : arrayLengths)
            length =+ i;
        Object newArray = Array.newInstance(expectedType.getComponentType(), length);
        for (int i = 0; i < length; i++)
            Array.set(newArray, i, newObjectDeserialization(expectedType.getComponentType(), stream, config)); // Iterate through array, all dimensions and all lengths.
        return newArray;
    }

    private Object readCollection(Class expectedType, InputStream stream, Configurations config) throws IOException {
        //List
        //Map
        //Else Collection
        int count = config.lengthConfig.toLength(stream);
        try {
            Collection collection = (Collection) expectedType.newInstance();
            for (int i = 0; i < count; i++){
                Object newObject = newObjectDeserialization(null, stream, config);
                collection.add(newObject);
            }
            return collection;
        } catch (IllegalAccessException | InstantiationException e){
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Object readObject(Class expectedType, InputStream stream, Configurations config) throws IOException, ParseException {
        try {
            Object obj = expectedType.newInstance();
            int count = config.lengthConfig.toLength(stream); // Field count.
            for (int i = 0; i < count; i++){
                if (stream.read() != FIELD) // Field TAG.
                    throw new IOException("Not a field.");
                String fieldName = TypeConverter.toString(read(stream, config), config.charEncoding);
                try {
                    Field field = expectedType.getField(fieldName);
                    if (field != null)
                        field.set(obj, newObjectDeserialization(field.getDeclaringClass(), stream, config)); // Field data deserializing
                    else
                        skipNextObject(stream, config); // No matching field, skip deserialization of this field.
                } catch (NoSuchFieldException e){
                    skipNextObject(stream, config); // No matching field, skip deserialization of this field.
                }
            }
            return obj;
        } catch (IllegalAccessException | InstantiationException e){
            e.printStackTrace();
        }
        return null;
    }

    private Object readPrimitive(Class type, InputStream stream, Configurations config) throws ParseException, IOException {
        if (type.isEnum()){
            String str = TypeConverter.toString(read(stream, config), config.charEncoding);
            for (Object e : type.getEnumConstants())
                if (e.toString().equals(str))
                    return e;
            throw new ParseException("No matching enum found for '" + str + "'.");
        } else {
            switch (primitives.get(type)){
                case Boolean:
                    return TypeConverter.toBoolean(read(stream, config, true));
                case Byte:
                    return read(stream, config, true)[0];
                case Double:
                    return TypeConverter.toDouble(read(stream, config, true));
                case Float:
                    return TypeConverter.toFloat(read(stream, config, true));
                case Short:
                    return TypeConverter.toShort(read(stream, config, true));
                case Integer:
                    return TypeConverter.toInteger(read(stream, config, true));
                case Long:
                    return TypeConverter.toLong(read(stream, config, true));
                case Char:
                    return TypeConverter.toChar(read(stream, config, true), config.charEncoding);
                case String:
                    return TypeConverter.toString(read(stream, config), config.charEncoding);
                default:
                    throw new ParseException(type.toString() + " is not a primitive.");
            }
        }
    }

    private byte[] read(InputStream stream, Configurations config) throws IOException {
        return read(stream, config, false);
    }
    private byte[] read(InputStream stream, Configurations config, boolean singleByteLength) throws IOException {
        int length;
        if (singleByteLength)
            length = stream.read();
        else {
            byte[] lengthBytes;
            switch (config.lengthConfig){
                case _8bit:
                    length = stream.read();
                    break;
                case _16bit:
                    lengthBytes = new byte[2];
                    stream.read(lengthBytes, 0, lengthBytes.length);
                    length = TypeConverter.toShort(lengthBytes);
                    break;
                case _32bit:
                    lengthBytes = new byte[4];
                    stream.read(lengthBytes, 0, lengthBytes.length);
                    length = TypeConverter.toInteger(lengthBytes);
                    break;
                default:
                    length = stream.read();
                    break;
            }
        }
        byte[] bytes = new byte[length];
        stream.read(bytes, 0, bytes.length);
        return bytes;
    }

    private void skipNextObject(InputStream stream, Configurations config) throws IOException {
        skip(stream, config); // Start skipping field.
    }
    private void skip(InputStream skipstream, Configurations skipconfig) throws IOException {
        int lastRead = skipstream.read();
        if (lastRead == CLASS){
            skipClass(skipstream, skipconfig);
            lastRead = skipstream.read();
            if (lastRead == CLASS)
                return;
        }
        if (lastRead == ARRAY)
            skipArray(skipstream, skipconfig);
        else if (lastRead == COLLECTION)
            skipCollection(skipstream, skipconfig);
        else if (lastRead == OBJECT)
            skipObject(skipstream, skipconfig);
        else if (binaryPrimitives.contains((byte)lastRead))
            skipPrimitive((byte)lastRead, skipstream, skipconfig);
    }
    private void skipClass(InputStream skipstream, Configurations skipconfig) throws IOException {
        read(skipstream, skipconfig);
    }
    private void skipArray(InputStream skipstream, Configurations skipconfig) throws IOException {
        int length = 0;
        if (skipstream.read() == ARRAYLENGTH)
            length = skipconfig.lengthConfig.toLength(skipstream);
        while (skipstream.read() == ARRAYLENGTH) // While reading Array TAGs, record the lengths of the array dimension.
            length = length * skipconfig.lengthConfig.toLength(skipstream); // Array length.
        for (int i = 0; i < length; i++)
            skip(skipstream, skipconfig);
    }
    private void skipCollection(InputStream skipstream, Configurations skipconfig) throws IOException {
        int count = skipstream.read(); // Collection count.
        for (int i = 0; i < count; i++)
            skip(skipstream, skipconfig);
    }
    private void skipObject(InputStream skipstream, Configurations skipconfig) throws IOException {
        int count = skipstream.read(); // Field count.
        for (int i = 0; i < count; i++){
            skipstream.read(); // Field TAG.
            read(skipstream, skipconfig);
            skip(skipstream, skipconfig);
        }
    }
    private void skipPrimitive(byte tag, InputStream skipstream, Configurations skipconfig) throws IOException {
        if (tag == ENUM || tag == STRING)
            read(skipstream, skipconfig);
        else
            read(skipstream, skipconfig);
    }
}
