package ru.isgaij.colorservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicsConfig {

    @Value("${app.kafka.topic.color-request}")
    private String colorRequestTopic;

    @Value("${app.kafka.topic.color-response}")
    private String colorResponseTopic;

    @Bean
    public NewTopic colorRequestTopic() {
        return TopicBuilder.name(colorRequestTopic)
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic colorResponseTopic() {
        return TopicBuilder.name(colorResponseTopic)
                .partitions(1)
                .replicas(1)
                .build();
    }
}
