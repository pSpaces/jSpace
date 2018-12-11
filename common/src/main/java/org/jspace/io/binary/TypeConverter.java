package org.jspace.io.binary;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class TypeConverter {
	public static byte[] getBytes(String obj, Configurations.CharEncodings encoding) {
		switch (encoding) {
			case UTF8:
				return StandardCharsets.UTF_8.encode(obj).array();
			case UTF16:
			case Unicode:
				return StandardCharsets.UTF_16.encode(obj).array();
			default:
				return null;
		}
	}
	public static String toString(byte[] bytes, Configurations.CharEncodings encoding) {
		switch (encoding) {
			case UTF8:
				return StandardCharsets.UTF_8.decode(ByteBuffer.wrap(bytes)).toString();
			case UTF16:
			case Unicode:
				return StandardCharsets.UTF_16.decode(ByteBuffer.wrap(bytes)).toString();
			default:
				return null;
		}
	}

	public static byte[] getBytes(short obj) {
		return ByteBuffer.allocate(Short.BYTES).putShort(obj).array();
	}
	public static short toShort(byte[] bytes) {
		return ByteBuffer.wrap(bytes).getShort();
	}

	public static byte[] getBytes(int obj) {
		return ByteBuffer.allocate(Integer.BYTES).putInt(obj).array();
	}
	public static int toInteger(byte[] bytes) {
		return ByteBuffer.wrap(bytes).getInt();
	}

	public static byte[] getBytes(boolean obj) {
		if (obj)
			return new byte[]{ 1 };
		else
			return new byte[]{ 0 };
	}
	public static Object toBoolean(byte[] bytes) {
		if (bytes[0] != 0)
			return true;
		else
			return false;
	}

	public static byte[] getBytes(long obj) {
		return ByteBuffer.allocate(Long.BYTES).putLong(obj).array();
	}
	public static long toLong(byte[] bytes) {
		return ByteBuffer.wrap(bytes).getLong();
	}

	public static byte[] getBytes(double obj) {
		return ByteBuffer.allocate(Double.BYTES).putDouble(obj).array();
	}
	public static double toDouble(byte[] bytes) {
		return ByteBuffer.wrap(bytes).getDouble();
	}

	public static byte[] getBytes(float obj) {
		return ByteBuffer.allocate(Float.BYTES).putFloat(obj).array();
	}
	public static float toFloat(byte[] bytes) {
		return ByteBuffer.wrap(bytes).getFloat();
	}

	public static byte[] getBytes(char obj, Configurations.CharEncodings encoding){
		return getBytes(String.valueOf(obj), encoding);
	}
	public static char toChar(byte[] bytes, Configurations.CharEncodings encoding) {
		return toString(bytes, encoding).charAt(0);
	}
}
