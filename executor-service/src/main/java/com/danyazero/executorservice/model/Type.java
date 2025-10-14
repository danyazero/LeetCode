package com.danyazero.executorservice.model;

public sealed interface Type  permits PrimitiveType, ArrayType {
    default boolean isArray() {
        return this instanceof ArrayType;
    }

    default boolean isEquals(SchemaType expected) {
        return getBaseType(this).schemaType() == expected;
    }

    static PrimitiveType getBaseType(Type type) {
        while (type instanceof ArrayType(Type elementType)) {
            type = elementType;
        }
        if (type instanceof PrimitiveType primitive) {
            return primitive;
        }
        throw new IllegalArgumentException("Unknown type: " + type);
    }
}
