package ru.isgaij.smartcloset.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class ItemForm {

    private Long id;

    @NotBlank(message = "Введите название вещи")
    @Size(min = 2, max = 100, message = "Название должно содержать от 2 до 100 символов")
    private String name;

    private String size;

    private String season;

    @DecimalMin(value = "0.0", inclusive = true, message = "Цена не может быть отрицательной")
    private BigDecimal price;

    @NotNull(message = "Выберите категорию")
    private Long categoryId;

    private Long brandId;

    private String color;

    private MultipartFile photo;
}
