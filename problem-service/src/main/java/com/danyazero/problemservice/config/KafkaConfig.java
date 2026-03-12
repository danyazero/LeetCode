package com.danyazero.problemservice.config;

import com.danyazero.problemservice.model.SubmissionUpdatedEvent;
import com.danyazero.problemservice.model.SubmissionUpdatedEventDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class KafkaConfig {
    @Value("${kafka.bootstrap-servers-config}")
    private  String bootstrapServers;

    @Bean
    public ConsumerFactory<String, SubmissionUpdatedEvent> consumerFactory() {
        Map<String, Object> props = new HashMap<>();

        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(JsonDeserializer.TYPE_MAPPINGS, getCompiledMapper(SubmissionUpdatedEvent.class, SubmissionUpdatedEventDto.class));

        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), new JsonDeserializer<>(SubmissionUpdatedEvent.class));
    }

    @Bean("submissionExecutionListenerContainerFactory")
    public KafkaListenerContainerFactory<?> submissionListenerContainerFactory(ConsumerFactory<String, SubmissionUpdatedEvent> consumerFactory) {
        var factory = new ConcurrentKafkaListenerContainerFactory<String, SubmissionUpdatedEvent>();
        factory.setConsumerFactory(consumerFactory);

        return factory;
    }

    private static String getCompiledMapper(Class<?>... mappers) {
        return Arrays.stream(mappers)
                .map(element -> element.getSimpleName() + ":" + element.getName())
                .collect(Collectors.joining(", "));
    }

}
