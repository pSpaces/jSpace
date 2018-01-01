package org.jspace.io.binary;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class TypeConverter {
	public static enum CharEncoding
    {
        UTF8, UTF16, Unicode
    }
	private Charset encoding = StandardCharsets.UTF_16LE;
	public void setCharacterEncoding(CharEncoding encoder){
		switch(encoder){
		case UTF8:
			encoding = StandardCharsets.UTF_8;
			break;
		case UTF16:
		case Unicode:
			encoding = StandardCharsets.UTF_16LE;
			break;
		default:
			break;
		}
	}

	public byte[] getBytes(String obj) {
		return obj.getBytes(encoding);
	}
	public String toString(byte[] bytes) {
		return new String(bytes, encoding);
	}

	public byte[] getBytes(short obj) {
		return ByteBuffer.allocate(Short.BYTES).putShort(obj).array();
	}
	public short toShort(byte[] bytes) {
		return ByteBuffer.wrap(bytes).getShort();
	}

	public byte[] getBytes(int obj) {
		return ByteBuffer.allocate(Integer.BYTES).putInt(obj).array();
	}
	public int toInteger(byte[] bytes) {
		return ByteBuffer.wrap(bytes).getInt();
	}

	public byte[] getBytes(boolean obj) {
		if (obj)
			return new byte[]{ 1 };
		else
			return new byte[]{ 0 };
	}
	public Object toBoolean(byte[] bytes) {
		if (bytes[0] != 0)
			return true;
		else
			return false;
	}

	public byte[] getBytes(long obj) {
		return ByteBuffer.allocate(Long.BYTES).putLong(obj).array();
	}
	public long toLong(byte[] bytes) {
		return ByteBuffer.wrap(bytes).getLong();
	}

	public byte[] getBytes(double obj) {
		return ByteBuffer.allocate(Double.BYTES).putDouble(obj).array();
	}
	public double toDouble(byte[] bytes) {
		return ByteBuffer.wrap(bytes).getDouble();
	}

	public byte[] getBytes(float obj) {
		return ByteBuffer.allocate(Float.BYTES).putFloat(obj).array();
	}
	public float toFloat(byte[] bytes) {
		return ByteBuffer.wrap(bytes).getFloat();
	}

	public byte[] getBytes(char obj){
		return encoding.encode(CharBuffer.wrap(new char[]{ obj })).array();
	}
	public char toChar(byte[] bytes) {
		return encoding.decode(ByteBuffer.wrap(bytes)).get();
	}
}
