package com.sineshore.serialization;

import static com.sineshore.serialization.ByteUtilities.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.LinkedHashMap;

public class Batch {

	// 2 - Size (Short)
	// ? - Name (String)
	// ? - Data (Bytes)

	private final String name;
	private final LinkedHashMap<String, Element> elements;

	public Batch(String name) {
		this.name = name;
		elements = new LinkedHashMap<>();
	}

	public Batch(byte[] bytes) {
		elements = new LinkedHashMap<>();
		int pointer = 0;
		short size = readShort(bytes, pointer);
		pointer += Short.BYTES;
		name = readString(bytes, pointer);
		int nameSize = name.getBytes().length;
		pointer += nameSize + Short.BYTES;
		while (pointer < size) {
			Element element = new Element(grabBytes(bytes, pointer, size - pointer));
			add(element);
			pointer += element.getSize();
		}
	}

	public Batch(InputStream inputStream) throws IOException {
		this(bytesFromInputStream(inputStream));
	}

	private static byte[] bytesFromInputStream(InputStream inputStream) throws IOException {
		int toRead = 2;
		int read = 0;
		byte[] bytes = null;
		while (read < toRead) {
			if (bytes == null) {
				bytes = new byte[toRead];
			}
			read += inputStream.read(bytes, read, (toRead - read));
			if (read == -1) {
				throw new IOException("end of stream");
			}
			if (toRead == 2 && toRead == read) {
				byte[] sizeBytes = bytes;
				short size = readShort(sizeBytes, 0);
				toRead = size;
				bytes = new byte[size];
				write(bytes, sizeBytes, 0);
			}
		}
		return bytes;
	}

	public boolean contains(String name) {
		return elements.containsKey(name);
	}

	public void add(Element element) {
		elements.put(element.getName(), element);
	}

	public void add(String name, Object value) {
		add(new Element(name, value));
	}

	public void removeElement(Element element) {
		elements.remove(element.getName(), element);
	}

	public void remove(String element) {
		removeElement(getElement(element));
	}

	public Element getElement(String name) {
		return elements.get(name);
	}

	public Collection<Element> getElements() {
		return elements.values();
	}

	public <T> T get(String name, Class<T> type) {
		if (!contains(name)) {
			throw new NullPointerException("batch does not contain key '" + name + "'");
		}
		Object value = elements.get(name).getValue();
		if (!type.isAssignableFrom(value.getClass())) {
			throw new IllegalStateException(
					"Unexpected type. Expected '" + type.getSimpleName() + "', got '" + value.getClass() + "'.");
		}
		return type.cast(value);
	}

	public Object get(String name) {
		return get(name, Object.class);
	}

	public String getName() {
		return name;
	}

	public byte[] toBytes() {
		short size = 0;
		byte[] bytes0 = Element.toBytes(size);
		byte[] bytes1 = Element.toBytes(name);

		size += bytes0.length;
		size += bytes1.length;

		for (Element element : elements.values()) {
			size += element.getSize();
		}

		byte[] bytes = new byte[size];
		bytes0 = Element.toBytes(size);

		int pointer = 0;
		pointer = write(bytes, bytes0, pointer);
		pointer = write(bytes, bytes1, pointer);
		for (Element element : elements.values()) {
			pointer = write(bytes, element.getBytes(), pointer);
		}
		return bytes;
	}

	public int getSize() {
		int size = 0;
		size += Short.BYTES;
		size += Element.toBytes(name).length;
		for (Element element : elements.values()) {
			size += element.getSize();
		}
		return size;
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(name + ":\n");
		elements.values().forEach(element -> buffer.append("\t" + element.toString().replace("\t", "\t\t") + "\n"));
		return buffer.toString();
	}

}
