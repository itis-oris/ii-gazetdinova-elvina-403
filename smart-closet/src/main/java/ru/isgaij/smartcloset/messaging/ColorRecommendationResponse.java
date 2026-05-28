package ru.isgaij.smartcloset.messaging;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ColorRecommendationResponse {
    private Long itemId;
    private String colorName;
    private String complementHex;
    private List<String> compatibleHexColors;
    private String message;
}
