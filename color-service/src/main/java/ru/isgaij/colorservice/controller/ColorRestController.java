package ru.isgaij.colorservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.isgaij.colorservice.dto.ColorInfoResponse;
import ru.isgaij.colorservice.service.ColorService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/colors")
@RequiredArgsConstructor
@Tag(name = "Цвета", description = "API для работы с цветовыми рекомендациями")
public class ColorRestController {

    private final ColorService colorService;

    @Operation(
            summary = "Получить информацию о цвете",
            description = "Возвращает название цвета, комплементарный цвет и список совместимых " +
                    "цветов для переданного HEX-кода. Поддерживает форматы с # и без, " +
                    "в любом регистре."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Информация о цвете успешно получена",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ColorInfoResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Некорректный HEX-код цвета",
                    content = @Content(mediaType = "application/json")
            )
    })
    @GetMapping("/info")
    public ResponseEntity<ColorInfoResponse> getColorInfo(
            @Parameter(
                    description = "HEX-код цвета (с # или без), например FF0000 или #FF0000",
                    required = true,
                    example = "FF0000"
            )
            @RequestParam String hex
    ) {
        log.debug("REST-запрос информации о цвете: hex={}", hex);

        if (hex == null || hex.isBlank()) {
            throw new IllegalArgumentException("Параметр 'hex' не может быть пустым");
        }

        String normalizedHex = colorService.normalize(hex);
        String name = colorService.resolveName(normalizedHex);
        String complement = colorService.complement(normalizedHex);
        List<String> compatible = colorService.compatibleColors(normalizedHex);

        ColorInfoResponse response = new ColorInfoResponse(normalizedHex, name, complement, compatible);

        log.debug("Ответ для hex={}: name={}, complement={}, compatible={}",
                normalizedHex, name, complement, compatible);

        return ResponseEntity.ok(response);
    }
}
