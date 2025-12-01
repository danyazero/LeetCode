package com.danyazero.submissionservice.config;

import com.danyazero.submissionservice.model.SubmissionCreatedEvent;
import com.danyazero.submissionservice.model.SubmissionUpdatedEvent;
import com.danyazero.submissionservice.model.SubmissionUpdatedEventDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

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
    public ProducerFactory<String, SubmissionCreatedEvent> producerFactory() {
        Map<String, Object> props = new HashMap<>();
        log.info("Creating ProducerFactory for Kafka config with bootstrapServers: {}", bootstrapServers);

        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        props.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, false);

        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean
    public KafkaTemplate<String, SubmissionCreatedEvent> submissionKafkaTemplate(
            ProducerFactory<String, SubmissionCreatedEvent> producerFactory
    ) {
        var kafkaTemplate = new KafkaTemplate<>(producerFactory);
        kafkaTemplate.setDefaultTopic("submissions");

        return kafkaTemplate;
    }


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
