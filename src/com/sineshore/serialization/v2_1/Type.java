package com.sineshore.serialization.v2_1;

public enum Type {

	/* 00 */ BYTE,
	/* 01 */ BOOLEAN,
	/* 02 */ SHORT,
	/* 03 */ CHAR,
	/* 04 */ INT,
	/* 05 */ LONG,
	/* 06 */ FLOAT,
	/* 07 */ DOUBLE,
	/* 08 */ STRING,
	/* 09 */ ARRAY,
	/* 10 */ OBJECT,
	/* 11 */ TERMINATION;

	public final byte id;

	private Type() {
		this.id = (byte) ordinal();
	}

	public static Type determineType(Object value) {
		if (value instanceof Byte) {
			return BYTE;
		} else if (value instanceof Boolean) {
			return Type.BOOLEAN;
		} else if (value instanceof Short) {
			return SHORT;
		} else if (value instanceof Character) {
			return CHAR;
		} else if (value instanceof Integer) {
			return INT;
		} else if (value instanceof Long) {
			return LONG;
		} else if (value instanceof Float) {
			return FLOAT;
		} else if (value instanceof Double) {
			return DOUBLE;
		} else if (value instanceof String) {
			return STRING;
		} else if (value.getClass().isArray()) {
			return ARRAY;
		} else {
			return OBJECT;
		}
	}

	public static final Type getType(byte id) {
		return values()[id];
	}

}
