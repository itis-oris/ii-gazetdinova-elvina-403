package ru.isgaij.colorservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ColorRecommendationRequest {

    private Long itemId;

    private String hex;

    private Long userId;
}
