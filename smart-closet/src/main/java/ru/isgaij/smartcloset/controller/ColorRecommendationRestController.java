package ru.isgaij.smartcloset.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.isgaij.smartcloset.client.ColorServiceClient;
import ru.isgaij.smartcloset.entity.Item;
import ru.isgaij.smartcloset.exception.ResourceNotFoundException;
import ru.isgaij.smartcloset.messaging.ColorRecommendationProducer;
import ru.isgaij.smartcloset.messaging.ColorRecommendationRequest;
import ru.isgaij.smartcloset.service.ItemService;

import java.util.Map;

@RestController
@RequestMapping("/api")
@Tag(name = "Colors", description = "Запрос имени цвета и пересчёт рекомендаций")
public class ColorRecommendationRestController {

    private final ColorServiceClient colorServiceClient;
    private final ColorRecommendationProducer producer;
    private final ItemService itemService;

    public ColorRecommendationRestController(ColorServiceClient colorServiceClient,
                                             ColorRecommendationProducer producer,
                                             ItemService itemService) {
        this.colorServiceClient = colorServiceClient;
        this.producer = producer;
        this.itemService = itemService;
    }

    @GetMapping("/colors/info")
    @Operation(summary = "Информация о цвете (через HTTP к color-service, Redis-кэш)")
    public Map<String, Object> info(@RequestParam String hex) {
        return colorServiceClient.getColorInfo(hex);
    }

    @PostMapping("/recommendations/{itemId}/refresh")
    @Operation(summary = "Пересчитать рекомендации (через Kafka + сбросить кэш)")
    @CacheEvict(cacheNames = "colorInfo", allEntries = true)
    public ResponseEntity<String> refresh(@PathVariable Long itemId) {
        Item item = itemService.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Вещь не найдена"));

        producer.send(new ColorRecommendationRequest(
                item.getId(),
                item.getColor(),
                item.getUser() != null ? item.getUser().getId() : null
        ));
        return ResponseEntity.accepted().body("Запрос на пересчёт отправлен в Kafka");
    }
}
