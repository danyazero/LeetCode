package com.danyazero.executorservice.generator;

import com.danyazero.executorservice.model.*;

import java.util.HashSet;

public class JavaSyntaxGenerator implements SyntaxGenerator {
    private final HashSet<String> imports = new HashSet<>();

    @Override
    public String wrapper(String className, String content) {
        return ("public class " + className + " {\n" + content + "}\n");
    }

    @Override
    public String getImports() {
        if (imports.isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (String importStatement : imports) {
            sb.append("import ").append(importStatement).append(";\n");
        }
        sb.append("\n");

        return sb.toString();
    }

    @Override
    public String invokeAndPrint(MethodSchema methodSchema) {
        if (methodSchema == null) {
            throw new IllegalArgumentException(
                    "JavaSyntaxGenerator -> Print value cannot be null"
            );
        }
        if (methodSchema.returnType().isArray()) {
            return (
                    "System.out.println(stringifyAny(" +
                            this.methodInvoke(methodSchema) +
                            "));\n"
            );
        }
        return "System.out.println(" + this.methodInvoke(methodSchema) + ");\n";
    }

    @Override
    public String start(String content) {
        imports.add("java.util.Arrays");
        imports.add("java.util.stream.Stream");

        var stringArrayType = new Type[]{new ArrayType(new PrimitiveType(SchemaType.STRING))};
        return (
                this.getImports() +
                        this.wrapper(
                                "Main",
                                this.function(
                                        new MethodSchema(
                                                "main",
                                                stringArrayType,
                                                new PrimitiveType(SchemaType.VOID)
                                        ),
                                        content
                                ) + UTIL_METHODS
                        )
        );
    }

    @Override
    public String generateArgsArray(int requiredSize) {
        if (requiredSize < 0) {
            throw new IllegalArgumentException(
                    "Required size cannot be negative: " + requiredSize
            );
        }

        return (
                "var args = fArg0;\n" +
                        "if (args.length < " +
                        requiredSize +
                        ") System.exit(1);\n"
        );
    }

    @Override
    public String nullValue() {
        return "null";
    }

    @Override
    public String function(MethodSchema schema, String content) {
        if (schema == null) {
            throw new IllegalArgumentException("MethodSchema cannot be null");
        }
        if (content == null) {
            content = "";
        }

        var isVoidType = schema.returnType().isEquals(SchemaType.VOID);
        var returnType = isVoidType
                ? "void"
                : typeToString(schema.returnType());

        var returnStatement = isVoidType
                ? ""
                : "return " + this.getDefaultReturnValue(schema.returnType()) + ";";

        return (
                "public static " +
                        returnType +
                        " " +
                        schema.name() +
                        "(" +
                        getFunctionArgs(schema.params()) +
                        ") {\n" +
                        content +
                        returnStatement +
                        "\n}\n"
        );
    }

    @Override
    public String methodInvoke(MethodSchema schema) {
        var invokeArgs = new String[schema.params().length];
        for (var i = 0; i < invokeArgs.length; i++) {
            invokeArgs[i] = "arg" + i;
        }

        return (
                capitalizeFirst(schema.name()) +
                        "." +
                        schema.name() +
                        "(" +
                        String.join(", ", invokeArgs) +
                        ")"
        );
    }

    @Override
    public String castFromString(Type toType, String into, String from) {
        if (toType == null || into == null || from == null) {
            throw new IllegalArgumentException(
                    "castFromString parameters cannot be null"
            );
        }

        if (toType instanceof PrimitiveType primitiveType) {
            return castPrimitiveFromString(primitiveType, into, from);
        } else if (toType instanceof ArrayType arrayType) {
            return castArrayFromString(arrayType, into, from);
        }

        return "";
    }

    private String castPrimitiveFromString(
            PrimitiveType primitiveType,
            String into,
            String from
    ) {
        String castMethod = getCastMethodByType(primitiveType.schemaType());

        if (castMethod.isEmpty()) {
            return "var " + into + " = " + from + ";\n";
        }

        return "var " + into + " = " + castMethod + "(" + from + ");\n";
    }

    private String castArrayFromString(
            ArrayType arrayType,
            String into,
            String from
    ) {

        PrimitiveType primitiveType = Type.getBaseType(arrayType);
        String javaType = getVariableType(primitiveType.schemaType());

        StringBuilder sb = new StringBuilder();

        sb
                .append("var ")
                .append(into)
                .append("ParsedArray = parse(")
                .append(from)
                .append(");\n");

        sb
                .append("var ")
                .append(into)
                .append(" = new ")
                .append(javaType)
                .append("[")
                .append(into)
                .append("ParsedArray.length];\n");

        sb
                .append("for (var i = 0; i < ")
                .append(into)
                .append("ParsedArray.length; i++) {\n");
        sb
                .append("\t")
                .append(
                        castFromString(
                                primitiveType,
                                into + "CastedValue",
                                into + "ParsedArray[i]"
                        )
                );
        sb
                .append("\t")
                .append(into)
                .append("[i] = ")
                .append(into)
                .append("CastedValue;\n");
        sb.append("}\n");

        return sb.toString();
    }

    private String getFunctionArgs(Type[] args) {
        if (args == null || args.length == 0) {
            return "";
        }

        var functionArgs = new String[args.length];

        for (var i = 0; i < functionArgs.length; i++) {
            functionArgs[i] = typeToString(args[i]) + " fArg" + i;
        }

        return String.join(", ", functionArgs);
    }

    @Override
    public String getVariableType(SchemaType type) {
        if (type == null) {
            throw new IllegalArgumentException("SchemaType cannot be null");
        }

        return switch (type) {
            case INTEGER -> "int";
            case BOOLEAN -> "boolean";
            case FLOAT -> "double";
            case STRING -> "String";
            case VOID -> "";
        };
    }

    @Override
    public String arrayType(String type) {
        if (type == null || type.isEmpty()) {
            throw new IllegalArgumentException("Type cannot be null or empty");
        }

        return type + "[]";
    }

    private static String getCastMethodByType(SchemaType type) {
        return switch (type) {
            case INTEGER -> "Integer.parseInt";
            case BOOLEAN -> "Boolean.parseBoolean";
            case FLOAT -> "Double.parseDouble";
            case STRING -> "";
            default -> "";
        };
    }

    private static String capitalizeFirst(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    private static final String UTIL_METHODS = """
            public static String[] parse(String input) {
                return Stream.of(input.split(","))
                    .map(String::trim)
                    .filter(el -> !el.isEmpty())
                    .toArray(String[]::new);
            }
            
            public static String stringifyAny(Object value) {
                if (value == null) return "null";
            
                if (value instanceof int[]) {
                    return Arrays.toString((int[]) value);
                } else if (value instanceof double[]) {
                    return Arrays.toString((double[]) value);
                } else if (value instanceof boolean[]) {
                    return Arrays.toString((boolean[]) value);
                } else if (value instanceof String[]) {
                    return Arrays.toString((String[]) value);
                } else {
                    return "Unsupported type";
                }
            }
            """;
}
