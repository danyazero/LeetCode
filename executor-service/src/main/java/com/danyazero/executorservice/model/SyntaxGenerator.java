package com.danyazero.executorservice.model;

public interface SyntaxGenerator {
    String nullValueToken();
    String visitArrayType(String type);
    String visitFunctionArgs(Type[] args);
    String getVariableType(SchemaType type);
    String visitWrapper(String className, String content);
    String visitFunctionDeclaration(MethodSchema schema, String content);


    default String typeToString(Type type) {
        if (type instanceof PrimitiveType(SchemaType schemaType)) {
            return this.getVariableType(schemaType);
        } else if (type instanceof ArrayType(Type elementType)) {
            return visitArrayType(typeToString(elementType));
        }
        throw new IllegalArgumentException("Unknown type: " + type);
    }

    default String getDefaultReturnValue(Type type) {
        if (type instanceof PrimitiveType(SchemaType schemaType)) {
            return switch (schemaType) {
                case INTEGER -> "0";
                case BOOLEAN -> "false";
                case FLOAT -> "0.0";
                case STRING -> "\"\"";
                case VOID -> "";
            };
        }

        return this.nullValueToken();
    }
}
