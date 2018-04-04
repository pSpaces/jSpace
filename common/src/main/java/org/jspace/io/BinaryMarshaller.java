package org.jspace.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import org.jspace.io.jSpaceMarshaller;
import org.jspace.io.binary.BinarySerializer;
import org.jspace.io.binary.TypeConverter;

public class BinaryMarshaller implements jSpaceMarshaller {

	private BinarySerializer serializer = new BinarySerializer();
	private TypeConverter typeconverter = new TypeConverter();
	
	public byte[] toByte(Object o) {
		try {
			return serializer.serialize(o);
		} catch(Exception e){
			return new byte[0];
		}
	}

	@SuppressWarnings("unchecked")
	public <T> T fromByte(Class<T> clazz, byte[] data) {
		try {
			return (T) serializer.deserialize(clazz, data);
		} catch(Exception e){
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public <T> T read(Class<T> clazz, BufferedReader reader) throws IOException {
		int next = 0;
		ArrayList<Byte> bytearray = new ArrayList<Byte>();
		while ((next = reader.read()) != -1) {
			byte[] ba = typeconverter.getBytes(next);
			for (byte b : ba)
				bytearray.add(b);
		}
		byte[] bytes = new byte[bytearray.size()];
		for (int i = 0; i < bytes.length; i++)
			bytes[i] = bytearray.get(i);
		try {
			return (T) serializer.deserialize(clazz, bytes);
		} catch (InstantiationException | IllegalAccessException e) {
			return null;
		}
	}

	public void write(Object o, PrintWriter writer) {
		try {
			byte[] bytes = serializer.serialize(o);
			String str = typeconverter.toString(bytes);
			writer.write(str);
		} catch (IllegalArgumentException | IllegalAccessException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
