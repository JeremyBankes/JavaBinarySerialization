package com.sineshore.serialization;

import java.nio.ByteBuffer;

public class ByteUtilities {

	public static final byte[] read(byte[] source, int pointer, int length) {
		byte[] result = new byte[length];
		for (int i = 0; i < length; i++) {
			result[i] = source[pointer + i];
		}
		return result;
	}

	public static final int write(byte[] destination, byte[] bytes, int pointer) {
		for (int i = 0; i < bytes.length; i++) {
			destination[pointer++] = bytes[i];
		}
		return pointer;
	}

	public static final int write(byte[] destination, byte bytes, int pointer) {
		destination[pointer++] = bytes;
		return pointer;
	}

	public static final int bytesRequired(String text) {
		return text.getBytes().length + 2;
	}

	public static final String readString(byte[] source, int pointer) {
		short byteLength = (short) (readShort(source, pointer) - Short.BYTES);
		return new String(grabBytes(source, pointer + Short.BYTES, byteLength));
	}

	public static final byte[] joinBytes(byte[]... sources) {
		int pointer = 0;
		for (byte[] source : sources)
			pointer += source.length;
		byte[] result = new byte[pointer];
		pointer = 0;
		for (int i = 0; i < sources.length; i++) {
			for (int j = 0; j < sources[i].length; j++) {
				result[pointer] = sources[i][j];
				pointer++;
			}
		}
		return result;
	}

	public static final byte[] grabBytes(byte[] source, int pointer, int length) {
		byte[] result = new byte[length];
		for (int i = 0; i < length; i++)
			result[i] = source[pointer + i];
		return result;
	}

	public static int sizeOf(Object value) {
		Class<?> dataType = value.getClass();
		if (dataType == byte.class || dataType == Byte.class) {
			return Byte.SIZE;
		}
		if (dataType == short.class || dataType == Short.class) {
			return Short.SIZE;
		}
		if (dataType == char.class || dataType == Character.class) {
			return Character.SIZE;
		}
		if (dataType == int.class || dataType == Integer.class) {
			return Integer.SIZE;
		}
		if (dataType == long.class || dataType == Long.class) {
			return Long.SIZE;
		}
		if (dataType == float.class || dataType == Float.class) {
			return Float.SIZE;
		}
		if (dataType == double.class || dataType == Double.class) {
			return Double.SIZE;
		}
		throw new IllegalStateException("Can't determine size of '" + dataType.getSimpleName() + "'.");
	}

	public static final byte[] bytes(byte value) {
		return new byte[] { value };
	}

	public static final byte[] bytes(boolean value) {
		return new byte[] { value ? (byte) 1 : 0 };
	}

	public static final byte[] bytes(short value) {
		return ByteBuffer.allocate(Short.BYTES).putShort(value).array();
	}

	public static final byte[] bytes(char value) {
		return ByteBuffer.allocate(Character.BYTES).putChar(value).array();
	}

	public static final byte[] bytes(int value) {
		return ByteBuffer.allocate(Integer.BYTES).putInt(value).array();
	}

	public static final byte[] bytes(long value) {
		return ByteBuffer.allocate(Long.BYTES).putLong(value).array();
	}

	public static final byte[] bytes(float value) {
		return ByteBuffer.allocate(Float.BYTES).putFloat(value).array();
	}

	public static final byte[] bytes(double value) {
		return ByteBuffer.allocate(Double.BYTES).putDouble(value).array();
	}

	public static final byte[] bytes(String value) {
		byte[] stringBytes = value.getBytes();
		byte[] bytes = new byte[stringBytes.length + Short.BYTES];
		int pointer = write(bytes, bytes((short) bytes.length), 0);
		write(bytes, stringBytes, pointer);
		return bytes;
	}

	public static final byte readByte(byte[] source, int pointer) {
		return source[pointer];
	}

	public static final boolean readBoolean(byte[] source, int pointer) {
		return source[pointer] == 1;
	}

	public static final short readShort(byte[] source, int pointer) {
		return ByteBuffer.wrap(source, pointer, 2).getShort();
	}

	public static final char readChar(byte[] source, int pointer) {
		return ByteBuffer.wrap(source, pointer, 2).getChar();
	}

	public static final int readInt(byte[] source, int pointer) {
		return ByteBuffer.wrap(source, pointer, 4).getInt();
	}

	public static final long readLong(byte[] source, int pointer) {
		return ByteBuffer.wrap(source, pointer, 8).getLong();
	}

	public static final float readFloat(byte[] source, int pointer) {
		return Float.intBitsToFloat(readInt(source, pointer));
	}

	public static final double readDouble(byte[] source, int pointer) {
		return Double.longBitsToDouble(readLong(source, pointer));
	}

}
