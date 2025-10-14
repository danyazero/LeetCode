package com.danyazero.executorservice.controller;

import com.danyazero.executorservice.generator.JavaSyntaxGenerator;
import com.danyazero.executorservice.model.MethodSchema;
import com.danyazero.executorservice.model.SchemaDto;
import com.danyazero.executorservice.utils.StartPointGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class Test {
    private final StartPointGenerator startPointGenerator;

    @PostMapping
    public String test(@RequestBody SchemaDto schemaDto) {
        if (schemaDto.language().equals("java")) {
            var javaGenerator = new JavaSyntaxGenerator();
            return startPointGenerator.apply(MethodSchema.parse(schemaDto.schema()), javaGenerator);
        }

        return null;
    }
}
