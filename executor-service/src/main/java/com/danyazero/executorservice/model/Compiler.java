package com.danyazero.executorservice.model;

public interface Compiler {
    String getLanguage();
    CompilationResult compile(String solution);
}
