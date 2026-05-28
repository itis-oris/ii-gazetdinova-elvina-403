package ru.isgaij.smartcloset.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@Schema(name = "WishlistItemRequest", description = "Запрос на сохранение желания")
public class WishlistItemRequest {

    @NotBlank(message = "Название обязательно")
    @Size(min = 1, max = 100, message = "Название должно быть от 1 до 100 символов")
    @Schema(example = "Белые кроссовки")
    private String name;

    @DecimalMin(value = "0.0", inclusive = true, message = "Цена не может быть отрицательной")
    @Schema(example = "5990.00")
    private BigDecimal price;

    @Size(max = 500)
    @Schema(example = "https://shop.com/sneakers")
    private String url;

    @Size(max = 500)
    @Schema(example = "Хочу к лету")
    private String note;
}
