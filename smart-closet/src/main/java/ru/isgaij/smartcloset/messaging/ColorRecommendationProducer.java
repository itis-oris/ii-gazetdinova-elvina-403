package ru.isgaij.smartcloset.messaging;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ColorRecommendationProducer {

    private static final Logger log = LoggerFactory.getLogger(ColorRecommendationProducer.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${app.kafka.topic.color-request}")
    private String requestTopic;

    public void send(ColorRecommendationRequest request) {
        log.info("[Kafka producer] -> topic={} payload={}", requestTopic, request);

        kafkaTemplate.send(requestTopic, String.valueOf(request.getItemId()), request);
    }
}
