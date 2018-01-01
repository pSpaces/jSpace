package org.jspace.io;

import java.nio.charset.StandardCharsets;

import org.jspace.io.jSpaceMarshaller;
import org.jspace.io.binary.BinarySerializer;

public class JSonMarshaller implements jSpaceMarshaller {

	private BinarySerializer serializer = new BinarySerializer();
	
	public byte[] toByte(Object o) {
		return serializer.serialize(o);
	}

	public <T> T fromByte(Class<T> clazz, byte[] data) {
		return (T) serializer.deserialize(clazz, o);
	}
	
	public <T> T read(Class<T> clazz, BufferedReader reader) throws IOException {
		return (T) serializer.deserialize(clazz, new ReaderInputStream(reader, StandardCharsets.UTF_8));
	}

	public void write(Object o, PrintWriter writer) {
		serializer.serialize(o, new WriterOutputStream(writer, StandardCharsets.UTF_8));
	}

}
