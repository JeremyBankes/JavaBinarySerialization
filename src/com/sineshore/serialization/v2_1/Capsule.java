package com.sineshore.serialization.v2_1;

import static com.sineshore.serialization.v2_1.ByteUtilities.*;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedHashMap;

public class Capsule {

	// 2 - Size (Short)
	// ? - Name (String)
	// ? - Data (Bytes)

	private final String name;
	private final LinkedHashMap<String, Batch> batches;

	public Capsule(String name) {
		this.name = name;
		batches = new LinkedHashMap<>();
	}

	public Capsule(byte[] bytes) {
		batches = new LinkedHashMap<>();
		int pointer = 0;
		short size = readShort(bytes, pointer);
		pointer += Short.BYTES;
		name = readString(bytes, pointer);
		int nameSize = name.getBytes().length;
		pointer += nameSize + Short.BYTES;
		while (pointer < size) {
			Batch batch = new Batch(grabBytes(bytes, pointer, size - pointer));
			add(batch);
			pointer += batch.getSize();
		}
	}

	public Capsule(File file) throws IOException {
		this(Files.readAllBytes(Paths.get(file.toURI())));
	}

	public Capsule(InputStream inputStream) throws IOException {
		byte[] sizeBytes = new byte[2];
		inputStream.read(sizeBytes);
		short size = readShort(sizeBytes, 0);
		byte[] bytes = new byte[size];
		inputStream.read(bytes, Short.BYTES, size - Short.BYTES);
		write(bytes, sizeBytes, 0);

		batches = new LinkedHashMap<>();
		int pointer = 0;
		pointer += Short.BYTES;
		name = readString(bytes, pointer);
		int nameSize = name.getBytes().length;
		pointer += nameSize + Short.BYTES;
		while (pointer < size) {
			Batch batch = new Batch(grabBytes(bytes, pointer, size - pointer));
			add(batch);
			pointer += batch.getSize();
		}
	}

	public boolean hasBatch(String name) {
		return batches.containsKey(name);
	}

	public void add(Batch batch) {
		batches.put(batch.getName(), batch);
	}

	public void removeBatch(Batch batch) {
		batches.remove(batch.getName(), batch);
	}

	public void removeBatch(String batch) {
		batches.remove(batch);
	}

	public Batch getBatch(String name) {
		return batches.get(name);
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

		for (Batch batch : batches.values()) {
			size += batch.getSize();
		}

		byte[] bytes = new byte[size];
		bytes0 = Element.toBytes(size);

		int pointer = 0;
		pointer = write(bytes, bytes0, pointer);
		pointer = write(bytes, bytes1, pointer);

		for (Batch batch : batches.values()) {
			pointer = write(bytes, batch.toBytes(), pointer);
		}
		return bytes;
	}

	public void writeToFile(File file) throws IOException {
		DataOutputStream outputStream = new DataOutputStream(new FileOutputStream(file));
		outputStream.write(toBytes());
		outputStream.close();
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(name + ":\n");
		batches.values().forEach(batch -> buffer.append("\t" + batch.toString().replace("\t", "\t\t") + "\n"));
		return buffer.toString();
	}

}
