package ru.isgaij.smartcloset.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiError {
    private int status;
    private String message;
    private String timestamp;

    private Map<String, String> fieldErrors;
}
