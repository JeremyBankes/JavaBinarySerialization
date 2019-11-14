package com.sineshore.serialization.v2_1;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;

public class DeserializedObject {

	private String name;
	private HashMap<String, DeserializedField> fields;

	public DeserializedObject(String name) {
		this.name = name;
		fields = new HashMap<>();
	}

	public String getName() {
		return name;
	}

	public Collection<DeserializedField> getFields() {
		return fields.values();
	}

	public <T> T occupy(T object) {
		for (Field field : object.getClass().getDeclaredFields()) {
			if (fields.containsKey(field.getName())) {
				field.setAccessible(true);
				Object fieldValue = fields.get(field.getName()).getValue();
				try {
					field.set(object, getWrapperClass(field.getType()).cast(fieldValue));
				} catch (IllegalArgumentException | IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
		return object;
	}

	public Object getField(String key) {
		return fields.get(key).getValue();
	}

	void addField(String name, Object value) {
		fields.put(name, new DeserializedField(name, value));
	}

	private static Class<?> getWrapperClass(Class<?> clazz) {
		if (clazz == byte.class) {
			return Integer.class;
		} else if (clazz == boolean.class) {
			return Boolean.class;
		} else if (clazz == short.class) {
			return Short.class;
		} else if (clazz == char.class) {
			return Character.class;
		} else if (clazz == int.class) {
			return Integer.class;
		} else if (clazz == long.class) {
			return Long.class;
		} else if (clazz == float.class) {
			return Float.class;
		} else if (clazz == double.class) {
			return Double.class;
		}
		return clazz;
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(name + ":\n");
		fields.values().forEach(field -> buffer.append("\t" + field + "\n"));
		return buffer.toString();
	}

	public static class DeserializedField {

		private final String name;
		private final Object value;

		public DeserializedField(String name, Object value) {
			this.name = name;
			this.value = value;
		}

		public String getName() {
			return name;
		}

		public Object getValue() {
			return value;
		}

		@Override
		public String toString() {
			return name + ": " + value;
		}

	}

}
