package com.danyazero.executorservice.model;

import java.util.Arrays;

public record MethodSchema(
        String name,
        Type[] params,
        Type returnType
) {
    public static MethodSchema parse(String schema) {
        validateSchema(schema);

        String[] parts = schema.split("->", -1);
        if (parts.length != 3) {
            throw new IllegalArgumentException(
                    "The schema must consist of three parts."
            );
        }

        String methodName = parseMethodName(parts[0]);
        Type[] params = parseMethodParams(parts[1]);
        Type returnType = parseType(parts[2]);

        return new MethodSchema(methodName, params, returnType);
    }

    private static void validateSchema(String schema) {
        if (schema == null || schema.isBlank()) {
            throw new IllegalArgumentException(
                    "Schema cannot be null or blank"
            );
        }

        if (!schema.contains("->")) {
            throw new IllegalArgumentException(
                    "Schema must contain '->' separator"
            );
        }

        if (schema.startsWith("->") || schema.endsWith("->")) {
            throw new IllegalArgumentException(
                    "Schema cannot start or end with '->'"
            );
        }

        if (schema.contains("-->")) {
            throw new IllegalArgumentException(
                    "Schema contains invalid '-->' sequence"
            );
        }
    }

    private static String parseMethodName(String namePart) {
        String name = namePart.trim();

        if (name.isEmpty()) {
            throw new IllegalArgumentException("Method name cannot be empty");
        }

        return name;
    }

    private static Type[] parseMethodParams(String paramsPart) {
        String trimmed = paramsPart.trim();

        if (trimmed.isEmpty()) {
            return new Type[0];
        }

        String[] paramStrings = trimmed.split(",");
        Type[] params = new Type[paramStrings.length];

        for (int i = 0; i < paramStrings.length; i++) {
            String param = paramStrings[i].trim();
            if (param.isEmpty()) {
                throw new IllegalArgumentException(
                        "Empty parameter at position " + i
                );
            }
            var parsedType = parseType(param);
            if (parsedType.isEquals(SchemaType.VOID)) {
                throw new IllegalArgumentException(
                        "Incorrect function parameter type: 'VOID'"
                );
            }
            params[i] = parsedType;
        }

        return params;
    }

    private static Type parseType(String typeStr) {
        String trimmed = typeStr.trim();

        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException("Type cannot be empty");
        }

        boolean isArray = trimmed.endsWith("[]");
        String baseTypeName = trimmed.replaceFirst("\\[\\]", "").trim();

        if (baseTypeName.isEmpty()) {
            throw new IllegalArgumentException(
                    "Type cannot be empty after removing array brackets"
            );
        }

        if (baseTypeName.contains("[") || baseTypeName.contains("]")) {
            throw new IllegalArgumentException(
                    "Malformed array type: " + typeStr + ". Invalid bracket syntax."
            );
        }

        baseTypeName = baseTypeName.trim();
        if (baseTypeName.isEmpty()) {
            throw new IllegalArgumentException(
                    "Base type name cannot be empty"
            );
        }

        SchemaType schemaType = parseSchemaType(baseTypeName);
        PrimitiveType primitiveType = new PrimitiveType(schemaType);

        return isArray ? new ArrayType(primitiveType) : primitiveType;
    }

    private static SchemaType parseSchemaType(String typeName) {
        try {
            return SchemaType.valueOf(typeName.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Unknown type: '" +
                            typeName +
                            "'. Valid types: " +
                            Arrays.toString(SchemaType.values())
            );
        }
    }
}
