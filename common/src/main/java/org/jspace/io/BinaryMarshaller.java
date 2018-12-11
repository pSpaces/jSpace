package org.jspace.io;

import org.jspace.io.binary.BinarySerializer;
import org.jspace.io.binary.exceptions.ParseException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class BinaryMarshaller implements jSpaceMarshaller {

	private BinarySerializer serializer = new BinarySerializer();
	
	public byte[] toByte(Object o) {
		try {
			return serializer.serialize(o);
		} catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}

	public <T> T fromByte(Class<T> clazz, byte[] data) {
		try {
			return (T) serializer.deserialize(clazz, data);
		} catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}

	public <T> T read(Class<T> clazz, InputStream input) throws IOException {
		try {
			T obj = (T) serializer.deserialize(clazz, input);
			return obj;
		} catch (ParseException e){
			throw new IOException(e);
		}
	}

	public void write(Object o, OutputStream output) {
		try {
			serializer.serialize(o, output);
		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}
	}

}
