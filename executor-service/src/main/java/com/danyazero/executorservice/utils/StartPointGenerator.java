package com.danyazero.executorservice.utils;

import com.danyazero.executorservice.model.MethodSchema;
import com.danyazero.executorservice.model.SyntaxGenerator;
import org.springframework.stereotype.Component;

import java.util.function.BiFunction;

@Component
public class StartPointGenerator implements BiFunction<MethodSchema, SyntaxGenerator, String> {

    @Override
    public String apply(MethodSchema methodSchema, SyntaxGenerator syntaxGenerator) {
        var args = syntaxGenerator.generateArgsArray(methodSchema.params().length);

        StringBuilder values = new StringBuilder();
        for (var i = 0; i < methodSchema.params().length; i++) {
            var currentValue = methodSchema.params()[i];
            values.append(syntaxGenerator.castFromString(
                    currentValue,
                    "arg" + i,
                    "args[" + i + "]"
            ));
        }

        var print = syntaxGenerator.invokeAndPrint(methodSchema);
        return syntaxGenerator.start(
                args,
                values.toString(),
                print
        );
    }
}
