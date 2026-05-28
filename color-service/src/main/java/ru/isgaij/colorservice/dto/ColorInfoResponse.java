package ru.isgaij.colorservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ColorInfoResponse {

    private String hex;

    private String name;

    private String complement;

    private List<String> compatible;
}
