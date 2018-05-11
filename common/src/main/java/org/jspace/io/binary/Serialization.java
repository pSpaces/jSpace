package org.jspace.io.binary;

import org.jspace.io.binary.exceptions.ParseException;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;

import static org.jspace.io.binary.BinarySerializer.*;

public class Serialization {
    private static HashMap<Class, Integer> primitives = new HashMap<>();
    private static HashMap<Class, Integer> initializePrimitives(){
        HashMap<Class, Integer> map = new HashMap<>();
        map.put(Boolean.class, 0);
        map.put(Byte.class, 1);
        map.put(Short.class, 2);
        map.put(Integer.class, 3);
        map.put(Long.class, 4);
        map.put(Float.class, 5);
        map.put(Double.class, 6);
        map.put(Character.class, 7);
        map.put(String.class, 8);
        return map;
    }

    void newObjectSerialization(Object obj, OutputStream stream, Configurations config) throws IOException, ParseException {
        if (writeClass(obj, stream, config))
            stream.write(CLASS);
        else if (primitives.containsKey(obj.getClass()) || obj.getClass().isEnum())
            writePrimitive(obj, stream, config);
        else if (obj.getClass().isArray())
            writeArray(obj, stream, config);
        else if (obj instanceof Collection)
            writeCollection(obj, stream, config);
        else
            writeObject(obj, stream, config);
    }

    private boolean writeClass(Object obj, OutputStream stream, Configurations config) throws IOException {
        boolean isAType = obj instanceof Class;
        if (isAType)
            write(CLASS, TypeConverter.getBytes(((Class)obj).getCanonicalName(), config.charEncoding), stream, config);
        else
            write(CLASS, TypeConverter.getBytes(obj.getClass().getCanonicalName(), config.charEncoding), stream, config);
        return isAType;
    }

    private void writeArray(Object obj, OutputStream stream, Configurations config) throws IOException, ParseException {
        stream.write(ARRAY); // Array TAG
        int length = Array.getLength(obj);
        writeCount(ARRAYLENGTH, length, stream, config);
        stream.write(0);
        for (int i = 0; i < length; i++)
            newObjectSerialization(Array.get(obj, i), stream, config); // Iterate through array, all dimensions and all lengths.
    }

    private void writeCollection(Object obj, OutputStream stream, Configurations config) throws IOException, ParseException {
        // List
        // Map
        // Else Collection
        Collection array = (Collection)obj;
        int length = array.size();
        writeCount(COLLECTION, length, stream, config);
        for (Object element : array)
            newObjectSerialization(element, stream, config); // Iterate through collection.
    }

    private void writeObject(Object obj, OutputStream stream, Configurations config) throws IOException, ParseException {
        Field[] fields = obj.getClass().getDeclaredFields();
        LinkedList<Field> fieldslist = new LinkedList<>();
        try {
            for (Field field : fields) // Sort out fields with the NotSerializable modifier and fields with null values.
                if (Serializable.class.isAssignableFrom(field.getDeclaringClass()) && field.isAccessible() && field.get(obj) != null)
                    fieldslist.add(field);
            for (Field field : fieldslist){
                write(FIELD, TypeConverter.getBytes(field.getName(), config.charEncoding), stream, config);
                newObjectSerialization(field.get(obj), stream, config); // Continue by serializing fields.
            }
        } catch (IllegalAccessException e) {
            // Should never be thrown.
            e.printStackTrace();
        }
    }

    private void writePrimitive(Object obj, OutputStream stream, Configurations config) throws ParseException, IOException {
        if (obj.getClass().isEnum())
            write(ENUM, TypeConverter.getBytes(obj.toString(), config.charEncoding), stream, config);
        else {
            switch (primitives.get(obj.getClass())){
                case 0: //Boolean b:
                    write(BOOL, TypeConverter.getBytes((Boolean) obj), stream, config, true);
                    break;
                case 1://Byte b:
                    write(INT, TypeConverter.getBytes((Byte) obj), stream, config, true);
                    break;
                case 2://Short s:
                    write(INT, TypeConverter.getBytes((Short) obj), stream, config, true);
                    break;
                case 3://Integer i:
                    write(INT, TypeConverter.getBytes((Integer) obj), stream, config, true);
                    break;
                case 4://Long l:
                    write(INT, TypeConverter.getBytes((Long) obj), stream, config, true);
                    break;
                case 5://Float f:
                    write(DOUBLE, TypeConverter.getBytes((Float) obj), stream, config, true);
                    break;
                case 6://Double d:
                    write(DOUBLE, TypeConverter.getBytes((Double)obj), stream, config, true);
                    break;
                case 7://Char c:
                    write(CHAR, TypeConverter.getBytes((Character) obj, config.charEncoding), stream, config, true);
                    break;
                case 8://String s:
                    write(STRING, TypeConverter.getBytes((String) obj, config.charEncoding), stream, config);
                    break;
                default:
                    throw new ParseException(obj.getClass().toString() + " is not a primitive.");
            }
        }
    }

    private void writeCount(byte tag, int count, OutputStream stream, Configurations config) throws IOException {
        stream.write(tag);
        byte[] bytes = config.lengthConfig.toBytes(count);
        stream.write(bytes, 0, bytes.length);
    }

    private void write(byte tag, byte[] bytes, OutputStream stream, Configurations config) throws IOException {
        write(tag, bytes, stream, config, false);
    }
    private void write(byte tag, byte[] bytes, OutputStream stream, Configurations config, boolean singleByteLength) throws IOException {
        byte[] length = singleByteLength ? new byte[] { (byte) bytes.length } : config.lengthConfig.toBytes(bytes.length);
        stream.write(tag);
        stream.write(length, 0, length.length);
        stream.write(bytes, 0, bytes.length);
    }
}
