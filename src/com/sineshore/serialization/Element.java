package com.sineshore.serialization;

import static com.sineshore.serialization.ByteUtilities.*;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;

public class Element {

	// 1 - Type (Byte)
	// 2 - Size (Short)
	// ? - Name (String)
	// ? - Data (Bytes)

	private final String name;
	private final byte[] bytes;
	private final Object value;
	private short size;

	public Element(byte[] bytes) {
		int pointer = 0;
		Type type = Type.getType(bytes[pointer]);
		pointer += 1;
		size = readShort(bytes, pointer);
		pointer += Short.BYTES;
		name = readString(bytes, pointer);
		pointer += name.getBytes().length + Short.BYTES;
		value = fromBytes(type, bytes, pointer);
		this.bytes = grabBytes(bytes, 0, size);
	}

	public Element(String name, Object value) {
		this.name = name;
		this.value = value;

		byte[] bytes0 = toBytes(Type.determineType(value).id);
		byte[] bytes1 = toBytes(size);
		byte[] bytes2 = toBytes(name);
		byte[] bytes3 = toBytes(value);

		size += bytes0.length;
		size += bytes1.length;
		size += bytes2.length;
		size += bytes3.length;

		bytes = new byte[size];
		bytes1 = toBytes(size);

		int pointer = 0;
		pointer = write(bytes, bytes0, pointer);
		pointer = write(bytes, bytes1, pointer);
		pointer = write(bytes, bytes2, pointer);
		pointer = write(bytes, bytes3, pointer);
	}

	static byte[] toBytes(Object value) {
		if (value instanceof Byte) {
			return bytes((Byte) value);
		} else if (value instanceof Boolean) {
			return bytes((Boolean) value);
		} else if (value instanceof Short) {
			return bytes((Short) value);
		} else if (value instanceof Character) {
			return bytes((Character) value);
		} else if (value instanceof Integer) {
			return bytes((Integer) value);
		} else if (value instanceof Long) {
			return bytes((Long) value);
		} else if (value instanceof Float) {
			return bytes((Float) value);
		} else if (value instanceof Double) {
			return bytes((Double) value);
		} else if (value instanceof String) {
			return bytes((String) value);
		} else {
			return serialize(value);
		}
	}

	static Object fromBytes(Type type, byte[] bytes, int pointer) {
		if (type == Type.BYTE) {
			return readByte(bytes, pointer);
		} else if (type == Type.BOOLEAN) {
			return readBoolean(bytes, pointer);
		} else if (type == Type.SHORT) {
			return readShort(bytes, pointer);
		} else if (type == Type.CHAR) {
			return readChar(bytes, pointer);
		} else if (type == Type.INT) {
			return readInt(bytes, pointer);
		} else if (type == Type.LONG) {
			return readLong(bytes, pointer);
		} else if (type == Type.FLOAT) {
			return readFloat(bytes, pointer);
		} else if (type == Type.DOUBLE) {
			return readDouble(bytes, pointer);
		} else if (type == Type.STRING) {
			return readString(bytes, pointer);
		} else if (type == Type.ARRAY) {
			return deserialize(grabBytes(bytes, pointer, bytes.length - pointer));
		} else if (type == Type.OBJECT) {
			return deserialize(grabBytes(bytes, pointer, bytes.length - pointer));
		} else {
			throw new IllegalStateException("Unhandled type: " + type);
		}
	}

	private static byte[] serialize(Object value) {
		ArrayList<Element> elements = new ArrayList<>();
		if (value.getClass().isArray()) {
			for (int i = 0; i < Array.getLength(value); i++) {
				elements.add(new Element(String.valueOf(i), Array.get(value, i)));
			}
		}
		String name = value.getClass().getSimpleName();
		Field[] fields = value.getClass().getDeclaredFields();
		for (Field field : fields) {
			if (field.isAnnotationPresent(Serialize.class)) {
				try {
					field.setAccessible(true);
					if (Modifier.isFinal(field.getModifiers())) {
						System.out.println("Attempted to serialize final field '" + field.getName() + "'.");
						continue;
					}
					if (field.get(value) != null) {
						elements.add(new Element(field.getName(), field.get(value)));
					}
				} catch (IllegalArgumentException | IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
		short size = 0;
		byte[] bytes0 = toBytes(size);
		byte[] bytes1 = toBytes(name);

		size += bytes0.length;
		size += bytes1.length;
		for (Element element : elements) {
			size += element.getSize();
		}

		byte[] bytes = new byte[size];
		bytes0 = toBytes(size);

		int pointer = 0;
		pointer = write(bytes, bytes0, pointer);
		pointer = write(bytes, bytes1, pointer);
		for (Element element : elements) {
			pointer = write(bytes, element.getBytes(), pointer);
		}

		return bytes;
	}

	private static Object deserialize(byte[] bytes) {
		int pointer = 0;
		short size = readShort(bytes, pointer);
		pointer += Short.BYTES;
		String name = readString(bytes, pointer);
		pointer += name.getBytes().length + Short.BYTES;
		DeserializedObject object = new DeserializedObject(name);
		while (pointer < size) {
			Element element = new Element(grabBytes(bytes, pointer, size - pointer));
			object.addField(element.getName(), element.getValue());
			pointer += element.getSize();
		}
		return object;
	}

	public String getName() {
		return name;
	}

	public short getSize() {
		return size;
	}

	public byte[] getBytes() {
		return bytes;
	}

	public Object getValue() {
		return value;
	}

	@Override
	public String toString() {
		return name + ": " + value;
	}

}
