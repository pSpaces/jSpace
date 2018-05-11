package org.jspace.io.binary;

import org.jspace.io.binary.Configurations.CharEncodings;
import org.jspace.io.binary.Configurations.LengthBits;
import org.jspace.io.binary.exceptions.ParseException;

import java.io.*;

public class BinarySerializer {
	public static final Byte // Primitive TAGs
			BOOL = 0b01100010, // UTF8 for 'b'
			INT = 0b01101001, // UTF8 for 'i'
			DOUBLE = 0b01100100, // UTF8 for 'd'
			CHAR = 0b01100011, // UTF8 for 'c'
			STRING = 0b01110011, // UTF8 for 's'
			ENUM = 0b01100101; // UTF8 for 'e'
	public static final Byte // TAGs
			CLASS = 0b01010100, // UTF8 for 'T'
			OBJECT = 0b01001111, // UTF8 for 'O'
			CLASSPARAMETER = 0b01010000, // UTF8 for 'P'
			FIELD = 0b01000110, // UTF8 for 'F'
			ARRAY = 0b01000001, // UTF8 for 'A'
			ARRAYLENGTH = 0b01001100, // UTF8 for 'L'
			COLLECTION = 0b01000011; // UTF8 for 'C'

	public LengthBits length = LengthBits._16bit;
	public CharEncodings charEncoding = CharEncodings.Unicode;

	private Serialization serialization = new Serialization();
	private Deserialization deserialization = new Deserialization();

	public byte[] serialize(Object obj) throws IOException, ParseException {
		try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
			serialize(obj, output);
			return output.toByteArray();
		}
	}

	public void serialize(Object obj, OutputStream stream) throws IOException, ParseException {
		Configurations config = new Configurations(length, charEncoding);
		stream.write(config.toByte());
		serialization.newObjectSerialization(obj, stream, config);
	}

	public Object deserialize(byte[] bytes) throws IOException, ParseException {
		return deserialize(null, bytes);
	}

	public Object deserialize(InputStream stream) throws IOException, ParseException {
		return deserialize(null, stream);
	}

	public Object deserialize(Class type, byte[] bytes) throws IOException, ParseException {
		try (ByteArrayInputStream input = new ByteArrayInputStream(bytes)) {
			return deserialize(type, input);
		}
	}

	public Object deserialize(Class type, InputStream stream) throws IOException, ParseException {
		int configurations = stream.read();
		if (configurations == -1)
			throw new IOException("Stream contains no data.");
		Configurations config = new Configurations((byte) configurations);
		Object obj = deserialization.newObjectDeserialization(type, stream, config);
		return obj;
	}
}
