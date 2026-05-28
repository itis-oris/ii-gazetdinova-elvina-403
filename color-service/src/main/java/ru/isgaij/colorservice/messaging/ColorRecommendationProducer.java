package ru.isgaij.colorservice.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.isgaij.colorservice.dto.ColorRecommendationResponse;

@Slf4j
@Component
@RequiredArgsConstructor
public class ColorRecommendationProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${app.kafka.topic.color-response}")
    private String responseTopic;

    public void sendResponse(ColorRecommendationResponse response) {
        String key = String.valueOf(response.getItemId());
        log.debug("Отправка цветовой рекомендации в топик '{}' для itemId={}", responseTopic, key);
        kafkaTemplate.send(responseTopic, key, response)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Ошибка отправки рекомендации для itemId={}: {}", key, ex.getMessage(), ex);
                    } else {
                        log.debug("Рекомендация для itemId={} успешно отправлена, offset={}",
                                key, result.getRecordMetadata().offset());
                    }
                });
    }
}
