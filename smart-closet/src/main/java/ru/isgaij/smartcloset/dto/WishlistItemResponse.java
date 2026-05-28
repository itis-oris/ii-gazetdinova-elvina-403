package ru.isgaij.smartcloset.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.isgaij.smartcloset.entity.WishlistItem;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@Schema(name = "WishlistItemResponse", description = "Элемент вишлиста")
public class WishlistItemResponse {

    private Long id;
    private String name;
    private BigDecimal price;
    private String url;
    private String note;
    private LocalDate addedDate;

    public static WishlistItemResponse from(WishlistItem entity) {
        WishlistItemResponse dto = new WishlistItemResponse();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setPrice(entity.getPrice());
        dto.setUrl(entity.getUrl());
        dto.setNote(entity.getNote());
        dto.setAddedDate(entity.getAddedDate());
        return dto;
    }
}
