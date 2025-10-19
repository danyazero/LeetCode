package com.danyazero.problemservice.config;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.boot.model.FunctionContributions;
import org.hibernate.dialect.PostgreSQLDialect;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PostgresDialect extends PostgreSQLDialect {
    @Override
    public void initializeFunctionRegistry(FunctionContributions functionContributions) {
        super.initializeFunctionRegistry(functionContributions);

        var typeConfiguration = functionContributions.getTypeConfiguration();
        var registry = functionContributions.getFunctionRegistry();

        registry
                .patternDescriptorBuilder("tsvector_match", "(?1 @@ ?2)")
                .setExactArgumentCount(2)
                .setInvariantType(typeConfiguration.getBasicTypeRegistry().resolve(StandardBasicTypes.BOOLEAN))
                .register();
    }
}
