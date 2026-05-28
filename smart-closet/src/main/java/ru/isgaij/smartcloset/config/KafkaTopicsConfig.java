package ru.isgaij.smartcloset.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicsConfig {

    @Bean
    public NewTopic colorRequestTopic(@Value("${app.kafka.topic.color-request}") String topic) {
        return TopicBuilder.name(topic).partitions(1).replicas(1).build();
    }

    @Bean
    public NewTopic colorResponseTopic(@Value("${app.kafka.topic.color-response}") String topic) {
        return TopicBuilder.name(topic).partitions(1).replicas(1).build();
    }
}
