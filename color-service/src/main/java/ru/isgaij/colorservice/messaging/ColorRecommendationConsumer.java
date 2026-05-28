package ru.isgaij.colorservice.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.isgaij.colorservice.dto.ColorRecommendationRequest;
import ru.isgaij.colorservice.dto.ColorRecommendationResponse;
import ru.isgaij.colorservice.service.ColorService;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ColorRecommendationConsumer {

    private final ColorService colorService;

    private final ColorRecommendationProducer producer;

    @KafkaListener(
            topics = "${app.kafka.topic.color-request}",
            groupId = "color-service",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleColorRequest(ColorRecommendationRequest request) {
        log.debug("Получен запрос на цветовую рекомендацию: itemId={}, hex={}, userId={}",
                request.getItemId(), request.getHex(), request.getUserId());

        try {
            String normalizedHex = colorService.normalize(request.getHex());
            String colorName = colorService.resolveName(normalizedHex);
            String complementHex = colorService.complement(normalizedHex);
            List<String> compatibleColors = colorService.compatibleColors(normalizedHex);

            String message = String.format(
                    "Цвет «%s» (#%s). Комплементарный: #%s. Совместимые: %s",
                    colorName, normalizedHex, complementHex, compatibleColors
            );

            ColorRecommendationResponse response = new ColorRecommendationResponse(
                    request.getItemId(),
                    colorName,
                    complementHex,
                    compatibleColors,
                    message
            );

            producer.sendResponse(response);

            log.debug("Ответ для itemId={} успешно сформирован и отправлен", request.getItemId());

        } catch (Exception e) {
            log.error("Ошибка обработки запроса для itemId={}: {}",
                    request.getItemId(), e.getMessage(), e);
        }
    }
}
