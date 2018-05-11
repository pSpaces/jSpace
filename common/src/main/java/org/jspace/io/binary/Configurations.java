package org.jspace.io.binary;

import java.io.IOException;
import java.io.InputStream;

public class Configurations {
    public enum LengthBits {
        _8bit, // Max length 127, 8bit (signed)
        _16bit, // Max length 32767, 16bit (signed)
        _32bit; // Max length 2147483647, 32bit (signed)

        byte[] toBytes(int count)
        {
            switch (this)
            {
                case _8bit:
                    return new byte[] { (byte)count };
                case _16bit:
                    return TypeConverter.getBytes((short)count);
                case _32bit:
                    return TypeConverter.getBytes(count);
                default:
                    return new byte[] { (byte)count };
            }
        }

        int toLength(InputStream stream) throws IOException {
            byte[] bytes;
            switch (this)
            {
                case _8bit:
                    return stream.read();
                case _16bit:
                    bytes = new byte[2];
                    stream.read(bytes, 0, bytes.length);
                    return TypeConverter.toShort(bytes);
                case _32bit:
                    bytes = new byte[4];
                    stream.read(bytes, 0, bytes.length);
                    return TypeConverter.toInteger(bytes);
                default:
                    return stream.read();
            }
        }
    }

    public enum CharEncodings
    {
        UTF8, UTF16, Unicode, UTF32
    }

    public LengthBits lengthConfig;
    public CharEncodings charEncoding;

    public Configurations(byte configByte){
        if ((configByte & 3) == 1) //Binary operations and equality
            charEncoding = CharEncodings.UTF8;
        else if ((configByte & 3) == 2)
            charEncoding = CharEncodings.UTF16;
        else
            charEncoding = CharEncodings.UTF32;
        if ((configByte & 12) == 4) //Binary operations and equality
            lengthConfig = LengthBits._8bit;
        else if ((configByte & 12) == 8)
            lengthConfig = LengthBits._16bit;
        else
            lengthConfig = LengthBits._32bit;
    }

    public Configurations(LengthBits lengthConfig, CharEncodings charEncoding){
        this.lengthConfig = lengthConfig;
        this.charEncoding = charEncoding;
    }

    public byte toByte()
    {
        int settings = 0;
        switch (charEncoding)
        {
            case UTF8:
                settings = settings | 1;
                break;
            case UTF16:
            case Unicode:
                settings = settings | 2;
                break;
            case UTF32:
                settings = settings | 3;
                break;
        }
        switch (lengthConfig)
        {
            case _8bit:
                settings = settings | 4;
                break;
            case _16bit:
                settings = settings | 8;
                break;
            case _32bit:
                settings = settings | 12;
                break;
        }
        return (byte)settings;
    }
}
