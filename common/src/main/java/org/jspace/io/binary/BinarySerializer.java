package org.jspace.io.binary;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import org.jspace.io.tools.ClassRegistry;
import org.jspace.io.tools.exceptions.ParseException;

public class BinarySerializer {

	private static enum Lengths
	{
		_8bit, // Max length 127, 8bit (signed)
		_16bit, // Max length 32767, 16bit (signed)
		_32bit // Max length 2147483647, 32bit (signed)
	}

	private static final Byte // Primitive TAGs
	BOOL = 0b01100010, // UTF8 for 'b'
	INT = 0b01101001, // UTF8 for 'i'
	DOUBLE = 0b01100100, // UTF8 for 'd'
	CHAR = 0b01100011, // UTF8 for 'c'
	STRING = 0b01110011, // UTF8 for 's'
	ENUM = 0b01100101; // UTF8 for 'e'
	private static final Byte // TAGs
	CLASS = 0b01010100, // UTF8 for 'T'
	OBJECT = 0b01001111, // UTF8 for 'O'
	CLASSPARAMETER = 0b01010000, // UTF8 for 'P'
	FIELD = 0b01000110, // UTF8 for 'F'
	ARRAY = 0b01000001, // UTF8 for 'A'
	ARRAYLENGTH = 0b01001100, // UTF8 for 'L'
	COLLECTION = 0b01000011; // UTF8 for 'C'

	private Lengths Length = Lengths._16bit;

	private enum PrimitiveTypes
	{
		Boolean, Byte, Double, Float, Short, Integer, Long, Character, String, Enum
	}

	private static final HashMap<Class<?>, PrimitiveTypes> primitives = constructPrimitives();
	private static HashMap<Class<?>, PrimitiveTypes> constructPrimitives(){
		HashMap<Class<?>, PrimitiveTypes> map = new HashMap<Class<?>, PrimitiveTypes>();
		map.put(Boolean.class, PrimitiveTypes.Boolean);
		map.put(Byte.class, PrimitiveTypes.Byte);
		map.put(Double.class, PrimitiveTypes.Double);
		map.put(Float.class, PrimitiveTypes.Float);
		map.put(Short.class, PrimitiveTypes.Short);
		map.put(Integer.class, PrimitiveTypes.Integer);
		map.put(Long.class, PrimitiveTypes.Long);
		map.put(Character.class, PrimitiveTypes.Character);
		map.put(String.class, PrimitiveTypes.String);
		map.put(Enum.class, PrimitiveTypes.Enum);
		map.put(boolean.class, PrimitiveTypes.Boolean);
		map.put(byte.class, PrimitiveTypes.Byte);
		map.put(double.class, PrimitiveTypes.Double);
		map.put(float.class, PrimitiveTypes.Float);
		map.put(short.class, PrimitiveTypes.Short);
		map.put(int.class, PrimitiveTypes.Integer);
		map.put(long.class, PrimitiveTypes.Long);
		map.put(char.class, PrimitiveTypes.Character);
		return map;
	}

	private static final HashSet<Byte> binaryprimitives = constructBinaryPrimitives();
	private static HashSet<Byte> constructBinaryPrimitives(){
		HashSet<Byte> set = new HashSet<Byte>();
		set.add(INT);
		set.add(DOUBLE);
		set.add(BOOL);
		set.add(CHAR);
		set.add(STRING);
		set.add(ENUM);
		return set;
	}

	private TypeConverter convertor = new TypeConverter();
	private int lastRead;

	public byte[] serialize(Object obj) throws IOException, IllegalArgumentException, IllegalAccessException, ParseException {
		try (ByteArrayOutputStream output = new ByteArrayOutputStream()){
			serialize(obj, output);
			output.flush();
			return output.toByteArray();
		}
	}
	public void serialize(Object obj, OutputStream output) throws IOException, IllegalArgumentException, IllegalAccessException, ParseException{
		newObjectSerialization(obj, output);
	}

	public Object deserialize(Class<?> type, byte[] bytes) throws IOException, InstantiationException, IllegalAccessException, ParseException{
		ByteArrayInputStream input = new ByteArrayInputStream(bytes);
		return deserialize(type, input);
	}
	public Object deserialize(Class<?> type, InputStream input) throws IOException, InstantiationException, IllegalAccessException, ParseException{
		lastRead = input.read();
		return newObjectDeserialization(new Class<?>[]{ type }, input);
	}

	private void classSerialization(Class<?> type, OutputStream output) throws IOException{
		if (primitives.containsKey(type)){
			output.write(primitiveToTAG(primitives.get(type)));
		} else if (type.isArray()){
			output.write(ARRAY); // Array TAG.
			int dimensions = 0;
			while (type.getComponentType() != null){
				dimensions++;
				type = type.getComponentType();
			}
			output.write(dimensions); // Array dimensions.
			classSerialization(type, output);
		} else {
			output.write(CLASS); // Class TAG.
            byte[] bytes = convertor.getBytes(type.getName());
            byte[] byteslength = lengthToBytes(bytes.length);
            output.write(byteslength, 0, byteslength.length); // Class name data length.
            output.write(bytes, 0, bytes.length); // Class name.
            byte[] bytes2 = convertor.getBytes(type.getTypeName());
            byte[] byteslength2 = lengthToBytes(bytes2.length);
            output.write(byteslength2, 0, byteslength2.length); // Class full name data length.
            output.write(bytes2, 0, bytes2.length); // Class full name.

//            TypeVariable<?>[] parametertypes = type.getTypeParameters();
//            if (parametertypes.length > 0)
//            {
//                output.write(CLASSPARAMETER); // Class parameter TAG.
//                output.write((byte)parametertypes.length); // Number of class parameters
//                for (TypeVariable<?> t : parametertypes)
//                	classSerialization((Class<?>)t.getClass(), output);
//            }
		}
	}
	private Class<?> classDeserialization(Class<?> expectedType, InputStream input) throws IOException{
		Class<?> result = null;
		if (binaryprimitives.contains((byte)lastRead)){
			if (lastRead == primitiveToTAG(primitives.get(expectedType)))
				result = expectedType;
			else
				throw new IOException(expectedType.toString() + " was expected, but was of unknown type.");
			lastRead = input.read();
			return result;
		} else if (Byte.compare((byte) lastRead, ARRAY) == 0){
			//if (!Enumeration.class.isAssignableFrom(expectedType))
			//	throw new IOException(expectedType.toString() + " was expected, but was an array.");
			int arrayRank = input.read(); // Array dimensions.
            lastRead = input.read();
            Class<?> type = expectedType;
            while (type.getComponentType() != null){
				type = type.getComponentType();
			}
            result = classDeserialization(type, input);
            return result;
		} else if (Byte.compare((byte) lastRead, CLASS) == 0){
			int length = readLength(input); // Class name data length.
            byte[] nameBytes = new byte[length];
            input.read(nameBytes, 0, nameBytes.length); // Class name.
            String className = convertor.toString(nameBytes);
            int fullLength = readLength(input); // Class full name data length.
            byte[] fullnameBytes = new byte[fullLength];
            input.read(fullnameBytes, 0, fullnameBytes.length); // Class full name.
            String fullclassName = convertor.toString(fullnameBytes);
            if (!ClassRegistry.containsValue(className)) { // Try to find the class in the Class Registry.
            	try {
                	result = Class.forName(className); // Try to get the type using the base name.
            	} catch (ClassNotFoundException e1){
            		try {
            			result = Class.forName(fullclassName); // Try to get the type using the full name.
            		} catch (ClassNotFoundException e2){
            			result = expectedType; // Nothing else works, try and parse data into the expected type given.
            		}
            	}
            } else {
            	result = ClassRegistry.reverse(className);
            }
            lastRead = input.read();
            if (lastRead == CLASSPARAMETER) // Class parameter TAG.
            {
                int count = input.read(); // Number of class parameters
                lastRead = input.read();
                Class<?>[] parametertypes = new Class<?>[count];
                for (int i = 0; i < count; i++)
                {
                    parametertypes[i] = classDeserialization(null, input);
                }
                result = expectedType; // No way to construct generic types in Java.
            }
            return result;
		} else throw new IOException();
	}

	private void newObjectSerialization(Object obj, OutputStream output) throws IOException, IllegalArgumentException, IllegalAccessException, ParseException{
		classSerialization(obj.getClass(), output);
		if (primitives.containsKey(obj.getClass()) || obj.getClass().isEnum()){
			byte[] bytePrimitives = primitiveToBytes(obj); // Primitive TAG, Data length, Primitive data.
			output.write(bytePrimitives); // Primitive data. int, bool, string etc.
		} else if (obj.getClass().isArray()){
			output.write(ARRAY);

			int dimensions = getNumberOfDimensions(obj.getClass());
			Object temp = obj;
			output.write(ARRAYLENGTH);
			output.write(lengthToBytes(Array.getLength(obj)));
			for (int i = 1; i < dimensions; i++){
				temp = Array.get(temp, i);
				output.write(ARRAYLENGTH);
				output.write(lengthToBytes(Array.getLength(temp)));
			}

			int[] indices = new int[dimensions];
			boolean c = true;
			while (c)
			{
				Object value = obj;
				for (int i = 0; i < indices.length; i++)
					value = Array.get(value, indices[i]);
				newObjectSerialization(value, output); // Iterate through array, all dimensions and all lengths.
				c = false;
				for (int i = 0; i < indices.length; i++)
				{
					indices[i]++;
					if (indices[i] < getLength(obj, i))
					{
						c = true;
						break;
					}
					else
						indices[i] = 0;
				}
			}
		} else if (Collection.class.isAssignableFrom(obj.getClass())){
			Collection<?> array = (Collection<?>)obj;
			output.write(COLLECTION); // Collection TAG.
			byte[] bytes = lengthToBytes(array.size());
			output.write(bytes); // Collection count.
			for (Object element : array)
			{
				newObjectSerialization(element, output); // Iterate through collection.
			}
		} else {
			Field[] fieldstemp = obj.getClass().getFields();
			Field[] classfieldstemp = obj.getClass().getDeclaredFields();
			Field[] fields = new Field[fieldstemp.length + classfieldstemp.length];
			System.arraycopy(fieldstemp, 0, fields, 0, fieldstemp.length);
			System.arraycopy(classfieldstemp, 0, fields, fieldstemp.length, classfieldstemp.length);
			Field.setAccessible(fields, true);
			ArrayList<Field> fieldslist = new ArrayList<Field>();
			for (Field field : fields)
				if (!fieldslist.contains(field) && !Modifier.isTransient(field.getModifiers()) && field.get(obj) != null)
					fieldslist.add(field);
			output.write(OBJECT);
			byte[] bytes = lengthToBytes(fieldslist.size());
			output.write(bytes);
			for (Field field : fieldslist){
				output.write(FIELD);
				byte[] fieldname = convertor.getBytes(field.getName());
				byte[] fieldnamelength = lengthToBytes(fieldname.length);
				output.write(fieldnamelength); // Field name data length.
				output.write(fieldname); // Field name data.
				newObjectSerialization(field.get(obj), output); // Continue by serializing fields.
			}
		}
	}
	@SuppressWarnings("unchecked")
	private Object newObjectDeserialization(Class<?>[] expectedtype, InputStream input) throws IOException, InstantiationException, IllegalAccessException, ParseException{
		if (lastRead == CLASS || lastRead == ARRAY || binaryprimitives.contains((byte)lastRead))
			expectedtype[0] = classDeserialization(expectedtype[0], input);
		if (binaryprimitives.contains((byte)lastRead)){
			Object result = readPrimitive(expectedtype[0], input);
			lastRead = input.read();
			return result;
		} else if (lastRead == ARRAY){
			LinkedList<Integer> arrayLengthsTemp = new LinkedList<Integer>();
			while ((lastRead = input.read()) == ARRAYLENGTH)
				arrayLengthsTemp.add(readLength(input));
			Class<?>[] parameters = new Class<?>[2];
			parameters[0] = Class.class;
			parameters[1] = int[].class;
			Object[] arguments = new Object[2];
			Class<?> componentType = expectedtype[0];
			while (componentType.getComponentType() != null)
				componentType = componentType.getComponentType();
			arguments[0] = componentType;
			int[] lengths = new int[arrayLengthsTemp.size()];
			for (int i = 0; i < arrayLengthsTemp.size(); i++)
				lengths[i] = arrayLengthsTemp.get(i);
			arguments[1] = lengths;
			Object newObj = null;
			try {
				Method method = Array.class.getDeclaredMethod("newInstance", parameters);
				newObj = method.invoke(null, arguments);
//
//				Class<?> componentType = newObj.getClass();
//				while (componentType.getComponentType() != null)
//					componentType = componentType.getComponentType();
				int[] indices = new int[arrayLengthsTemp.size()];
				boolean c = true;
				while (c)
				{
					Object value = newObjectDeserialization(new Class<?>[]{ componentType }, input);
					Object element = newObj;
					for (int i = 0; i < indices.length-1; i++)
						element = Array.get(element, indices[i]);
					Array.set(element, indices[indices.length-1], value);
					c = false;
					for (int i = 0; i < indices.length; i++)
					{
						indices[i]++;
						if (indices[i] < getLength(newObj, i))
						{
							c = true;
							break;
						}
						else
							indices[i] = 0;
					}
				}
			} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace();
			}
			return newObj;
		} else if (lastRead == COLLECTION){
			int count = readLength(input); // Collection count.
			lastRead = input.read(); // New TAG.
			@SuppressWarnings("rawtypes")
			Collection collection = (Collection) expectedtype[0].newInstance();
			for (int i = 0; i < count; i++) // Reconstruct collection through iteration.
			{
				Object newObject = newObjectDeserialization(new Class<?>[]{ expectedtype[1] }, input);
				collection.add(newObject);
			}
			return collection;
		} else if (lastRead == OBJECT){
			Object obj = expectedtype[0].newInstance();
			int count = readLength(input); // Field count.
			lastRead = input.read(); // Field TAG.
			for (int i = 0; i < count; i++){
				if (lastRead != FIELD)
					throw new IOException("Not a field.");
				int length = readLength(input); // Field name data length.
				byte[] bytes = new byte[length];
				input.read(bytes, 0, length); // Field name data
				String fieldname = convertor.toString(bytes);
				Field field = null;
				try {
					field = expectedtype[0].getField(fieldname);
				} catch (NoSuchFieldException | SecurityException e) {
					try {
						field = expectedtype[0].getDeclaredField(fieldname);
					} catch (NoSuchFieldException | SecurityException e1) {
						e1.printStackTrace();
					}
				}
				lastRead = input.read(); // New TAG
				if (field != null){
					if (!field.isAccessible())
						field.setAccessible(true);
					if (ParameterizedType.class.isAssignableFrom(field.getGenericType().getClass())){
						ParameterizedType listType = (ParameterizedType) field.getGenericType();
				        Class<?> listClass = (Class<?>) listType.getActualTypeArguments()[0];
				        field.set(obj, newObjectDeserialization(new Class<?>[]{ field.getType(), listClass }, input)); // Field data deserializing.
					}
					else
						field.set(obj, newObjectDeserialization(new Class<?>[]{ field.getType() }, input)); // Field data deserializing.
				} else
					skipField(input); // No matching field, skip deserialization of this field.
			}
			return obj;
		} else
			throw new IOException("Error. Invalid inputstream.");
	}

	private Integer[] readArrayLengths(InputStream input) throws IOException{
		LinkedList<Integer> arrayLengthsTemp = new LinkedList<Integer>();
		while (lastRead == ARRAY)
		{
			arrayLengthsTemp.add(input.read());
			lastRead = input.read();
		}
		return arrayLengthsTemp.toArray(new Integer[0]);
	}

	private void skipField(InputStream input){
		skip(input); // Start skipping field.
	}
	private void skip(InputStream i)
	{
		if (lastRead == ARRAY)
			skipArray(i);
		if (lastRead == COLLECTION)
			skipCollection(i);
		if (lastRead == OBJECT)
			skipObject(i);
		else
			skipPrimitive(i);
	}
	private void skipArray(InputStream i)
	{
		try {
			int length = 0;
			if ((lastRead = i.read()) == ARRAYLENGTH)
				length = readLength(i);
			while ((lastRead = i.read()) == ARRAYLENGTH) // While reading Array TAGs, record the lengths of the array dimension.
				length = length * readLength(i); // Array length.
			for (int j = 0; j < length; j++)
				skip(i);
		} catch (IOException e){
			e.printStackTrace();
		}
	}
	private void skipCollection(InputStream i)
	{
		try {
			int count = i.read(); // Collection count.
			lastRead = i.read();
			for (int j = 0; j < count; j++)
			{
				skip(i);
			}
		} catch (IOException e){
			e.printStackTrace();
		}
	}
	private void skipObject(InputStream i)
	{
		try {
			int count = i.read(); // Field count.
			for (int j = 0; j < count; j++)
			{
				i.read(); // Field TAG.
				int length = readLength(i); // Length.
				i.skip(length); // Field name.
				lastRead = i.read();
				skip(i);
			}
		} catch (IOException e){
			e.printStackTrace();
		}
	}
	private void skipPrimitive(InputStream i)
	{
		try {
			int length = i.read();
			i.skip(length);
			lastRead = i.read();
		} catch (IOException e){
			e.printStackTrace();
		}
	}

	private byte[] lengthToBytes(int length){
		byte[] lengthbytes;
		switch (Length)
		{
		case _8bit:
			lengthbytes = new byte[] { (byte)length };
			break;
		case _16bit:
			lengthbytes = convertor.getBytes((short)length);
			break;
		case _32bit:
			lengthbytes = convertor.getBytes(length);
			break;
		default:
			lengthbytes = new byte[] { (byte)length };
			break;
		}
		return lengthbytes;
	}

	private int readLength(InputStream input) throws IOException{
		byte[] lengthbytes;
		int length;
		switch (Length)
		{
		case _8bit:
			length = input.read();
			break;
		case _16bit:
			lengthbytes = new byte[2];
			input.read(lengthbytes, 0, lengthbytes.length);
			length = convertor.toShort(lengthbytes);
			break;
		case _32bit:
			lengthbytes = new byte[4];
			input.read(lengthbytes, 0, lengthbytes.length);
			length = convertor.toInteger(lengthbytes);
			break;
		default:
			length = input.read();
			break;
		}
		return length;
	}

	private byte[] primitiveToBytes(Object obj) throws ParseException{
		byte type;
		byte[] length;
		byte[] data;
		if (obj.getClass().isEnum())
		{
			data = convertor.getBytes(obj.toString());
			length = lengthToBytes(data.length);
			type = ENUM;
		}
		else
		{
			switch (primitives.get(obj.getClass()))
			{
			case Boolean:
				data = convertor.getBytes((boolean)obj);
				length = new byte[] { (byte)(data.length) };
				type = BOOL;
				break;
			case Byte:
				data = new byte[] { (byte)obj };
				length = new byte[] { (byte)(data.length) };
				type = INT;
				break;
			case Short:
				data = convertor.getBytes((short)obj);
				length = new byte[] { (byte)(data.length) };
				type = INT;
				break;
			case Integer:
				data = convertor.getBytes((int)obj);
				length = new byte[] { (byte)(data.length) };
				type = INT;
				break;
			case Long:
				data = convertor.getBytes((long)obj);
				length = new byte[] { (byte)(data.length) };
				type = INT;
				break;
			case Float:
				data = convertor.getBytes((float)obj);
				length = new byte[] { (byte)(data.length) };
				type = DOUBLE;
				break;
			case Double:
				data = convertor.getBytes((double)obj);
				length = new byte[] { (byte)(data.length) };
				type = DOUBLE;
				break;
			case Character:
				data = convertor.getBytes((char)obj);
				length = new byte[] { (byte)(data.length) };
				type = CHAR;
				break;
			case String:
				data = convertor.getBytes((String)obj);
				length = lengthToBytes(data.length);
				type = STRING;
				break;
			default:
				throw new ParseException(obj.getClass().toString() + " is not a primitive.");
			}
		}
		byte[] bytes = new byte[data.length + length.length + 1];
		bytes[0] = type;
		System.arraycopy(length, 0, bytes, 1, length.length);
		System.arraycopy(data, 0, bytes, length.length + 1, data.length);
		return bytes;
	}

	private Object readPrimitive(Class<?> type, InputStream input) throws IOException, ParseException{
		int length;
		byte[] bytes;
		if (type.isEnum())
		{
			byte[] lengthbytes;
			switch (Length) // Primitive data length.
			{
			case _8bit:
				length = input.read();
				break;
			case _16bit:
				lengthbytes = new byte[2];
				input.read(lengthbytes, 0, lengthbytes.length);
				length = convertor.toShort(lengthbytes);
				break;
			case _32bit:
				lengthbytes = new byte[4];
				input.read(lengthbytes, 0, lengthbytes.length);
				length = convertor.toInteger(lengthbytes);
				break;
			default:
				length = input.read();
				break;
			}
			bytes = new byte[length];
			input.read(bytes, 0, length); // Primitive data. int, bool, string etc.
			String str = convertor.toString(bytes);
			for (Object e : type.getEnumConstants())
			{
				if (e.toString().equals(str))
					return e;
			}
			throw new ParseException("No matching enum found for '" + str + "'.");
		}
		else
		{
			switch (primitives.get(type))
			{
			case Boolean:
				length = input.read(); // Primitive data length.
				bytes = new byte[length];
				input.read(bytes, 0, length); // Primitive data. int, bool, string etc.
				return convertor.toBoolean(bytes);
			case Byte:
				length = input.read(); // Primitive data length.
				bytes = new byte[length];
				input.read(bytes, 0, length); // Primitive data. int, bool, string etc.
				return bytes[0];
			case Short:
				length = input.read(); // Primitive data length.
				bytes = new byte[length];
				input.read(bytes, 0, length); // Primitive data. int, bool, string etc.
				return convertor.toShort(bytes);
			case Integer:
				length = input.read(); // Primitive data length.
				bytes = new byte[length];
				input.read(bytes, 0, length); // Primitive data. int, bool, string etc.
				return convertor.toInteger(bytes);
			case Long:
				length = input.read(); // Primitive data length.
				bytes = new byte[length];
				input.read(bytes, 0, length); // Primitive data. int, bool, string etc.
				return convertor.toLong(bytes);
			case Float:
				length = input.read(); // Primitive data length.
				bytes = new byte[length];
				input.read(bytes, 0, length); // Primitive data. int, bool, string etc.
				return convertor.toFloat(bytes);
			case Double:
				length = input.read(); // Primitive data length.
				bytes = new byte[length];
				input.read(bytes, 0, length); // Primitive data. int, bool, string etc.
				return convertor.toDouble(bytes);
			case Character:
				length = input.read(); // Primitive data length.
				bytes = new byte[length];
				input.read(bytes, 0, length); // Primitive data. int, bool, string etc.
				return convertor.toChar(bytes);
			case String:
				byte[] lengthbytes;
				switch (Length)
				{
				case _8bit:
					length = input.read();
					break;
				case _16bit:
					lengthbytes = new byte[2];
					input.read(lengthbytes, 0, lengthbytes.length);
					length = convertor.toShort(lengthbytes);
					break;
				case _32bit:
					lengthbytes = new byte[4];
					input.read(lengthbytes, 0, lengthbytes.length);
					length = convertor.toInteger(lengthbytes);
					break;
				default:
					length = input.read();
					break;
				}
				bytes = new byte[length];
				input.read(bytes, 0, length); // Primitive data. int, bool, string etc.
				return convertor.toString(bytes);
			default:
				throw new ParseException(type.toString() + " is not a primitive.");
			}
		}
	}

	private byte primitiveToTAG(PrimitiveTypes primitive) throws IllegalArgumentException{
		switch (primitive)
		{
		case Boolean:
			return BOOL;
		case Character:
			return CHAR;
		case Double:
		case Float:
			return DOUBLE;
		case Byte:
		case Short:
		case Integer:
		case Long:
			return INT;
		case String:
			return STRING;
		case Enum:
			return ENUM;
		default:
			throw new IllegalArgumentException("Illegal Argument.");
		}
	}

	private int getNumberOfDimensions(Class<?> classObj) {
		int count = 0;
		while ((classObj = classObj.getComponentType()) != null)
			count++;
		return count;
	}

	private int getLength(Object obj, int i) {
		for (int j = 0; j < i; j++)
			obj = Array.get(obj, 0);
		return Array.getLength(obj);
	}
}
