package com.danyazero.executorservice.model;

public interface SyntaxGenerator {
    String nullValue();
    String getImports();
    String start(String content);
    String arrayType(String type);
    String invokeAndPrint(MethodSchema schema);
    String getVariableType(SchemaType type);
    String methodInvoke(MethodSchema schema);
    String generateArgsArray(int requiredSize);
    String wrapper(String className, String content);
    String function(MethodSchema schema, String content);
    String castFromString(Type toType, String into, String from);

    default String start(String... content) {
        return this.start(String.join("\n", content));
    }

    default String typeToString(Type type) {
        if (type instanceof PrimitiveType(SchemaType schemaType)) {
            return this.getVariableType(schemaType);
        } else if (type instanceof ArrayType(Type elementType)) {
            return arrayType(typeToString(elementType));
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

        return this.nullValue();
    }

}
