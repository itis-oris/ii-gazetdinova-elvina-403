package ru.isgaij.smartcloset.messaging;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.isgaij.smartcloset.service.ItemService;

@Component
@RequiredArgsConstructor
public class ColorRecommendationConsumer {

    private static final Logger log = LoggerFactory.getLogger(ColorRecommendationConsumer.class);

    private final ItemService itemService;

    @KafkaListener(
            topics = "${app.kafka.topic.color-response}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void onResponse(ColorRecommendationResponse response) {
        log.info("[Kafka consumer] <- color response: {}", response);

        if (response == null || response.getItemId() == null) return;

        itemService.applyColorRecommendation(
                response.getItemId(),
                response.getColorName(),
                response.getComplementHex()
        );
    }
}
