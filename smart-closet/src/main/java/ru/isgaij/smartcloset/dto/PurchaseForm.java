package ru.isgaij.smartcloset.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class PurchaseForm {

    @NotNull(message = "Выберите вещь")
    private Long itemId;

    @NotNull(message = "Укажите дату покупки")
    private LocalDate purchaseDate;

    private BigDecimal price;

    private String note;
}