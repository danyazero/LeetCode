package com.danyazero.executorservice.generator;

import com.danyazero.executorservice.model.*;

public class JavaSyntaxGenerator implements SyntaxGenerator {

    @Override
    public String visitWrapper(String className, String content) {
        return ("public class " + className + " {\n" + content + "}\n");
    }

    @Override
    public String visitArrayType(String type) {
        if (type == null || type.isEmpty()) {
            throw new IllegalArgumentException("Type cannot be null or empty");
        }

        return type + "[]";
    }

    @Override
    public String nullValueToken() {
        return "null";
    }

    @Override
    public String visitFunctionDeclaration(MethodSchema schema, String content) {
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
                        visitFunctionArgs(schema.params()) +
                        ") {\n" +
                        content +
                        returnStatement +
                        "\n}\n"
        );
    }

    @Override
    public String visitFunctionArgs(Type[] args) {
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
}
