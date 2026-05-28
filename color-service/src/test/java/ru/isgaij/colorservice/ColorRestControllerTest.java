package ru.isgaij.colorservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.isgaij.colorservice.controller.ColorRestController;
import ru.isgaij.colorservice.exception.RestExceptionHandler;
import ru.isgaij.colorservice.service.ColorService;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ColorRestController.class)
@Import({ColorService.class, RestExceptionHandler.class})
@DisplayName("Тесты REST-контроллера ColorRestController")
class ColorRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;



    @Test
    @DisplayName("GET /api/colors/info?hex=FF0000 → 200, name=Красный")
    void getColorInfo_redHex_returnsRedName() throws Exception {
        mockMvc.perform(get("/api/colors/info")
                        .param("hex", "FF0000")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.hex").value("FF0000"))
                .andExpect(jsonPath("$.name").value("Красный"))
                .andExpect(jsonPath("$.complement").value("00FFFF"))
                .andExpect(jsonPath("$.compatible").isArray())
                .andExpect(jsonPath("$.compatible", hasSize(3)));
    }

    @Test
    @DisplayName("GET /api/colors/info?hex=0000FF → 200, name=Синий")
    void getColorInfo_blueHex_returnsBlueName() throws Exception {
        mockMvc.perform(get("/api/colors/info")
                        .param("hex", "0000FF"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Синий"))
                .andExpect(jsonPath("$.complement").value("FFFF00"));
    }

    @Test
    @DisplayName("GET /api/colors/info?hex=FFFFFF → 200, name=Белый")
    void getColorInfo_whiteHex_returnsWhiteName() throws Exception {
        mockMvc.perform(get("/api/colors/info")
                        .param("hex", "FFFFFF"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Белый"))
                .andExpect(jsonPath("$.complement").value("000000"));
    }

    @Test
    @DisplayName("GET /api/colors/info?hex=#ff0000 → 200, нормализация выполнена корректно")
    void getColorInfo_hexWithHashAndLowerCase_normalizes() throws Exception {
        mockMvc.perform(get("/api/colors/info")
                        .param("hex", "#ff0000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hex").value("FF0000"))
                .andExpect(jsonPath("$.name").value("Красный"));
    }

    @Test
    @DisplayName("GET /api/colors/info?hex=808080 → 200, name=Серый")
    void getColorInfo_grayHex_returnsGrayName() throws Exception {
        mockMvc.perform(get("/api/colors/info")
                        .param("hex", "808080"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Серый"));
    }

    @Test
    @DisplayName("GET /api/colors/info?hex=FFA500 → 200, name=Оранжевый")
    void getColorInfo_orangeHex_returnsOrangeName() throws Exception {
        mockMvc.perform(get("/api/colors/info")
                        .param("hex", "FFA500"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Оранжевый"));
    }



    @Test
    @DisplayName("GET /api/colors/info без параметра hex → 400")
    void getColorInfo_missingHexParam_returns400() throws Exception {
        mockMvc.perform(get("/api/colors/info"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/colors/info?hex= (пустой hex) → 400 с JSON-ошибкой")
    void getColorInfo_emptyHex_returns400WithError() throws Exception {
        mockMvc.perform(get("/api/colors/info")
                        .param("hex", "   "))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }



    @Test
    @DisplayName("GET /api/colors/info → ответ содержит все обязательные поля")
    void getColorInfo_response_hasAllRequiredFields() throws Exception {
        mockMvc.perform(get("/api/colors/info")
                        .param("hex", "00FF00"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hex").exists())
                .andExpect(jsonPath("$.name").exists())
                .andExpect(jsonPath("$.complement").exists())
                .andExpect(jsonPath("$.compatible").exists());
    }

    @Test
    @DisplayName("GET /api/colors/info → compatible содержит ровно 3 цвета")
    void getColorInfo_compatible_hasExactlyThreeColors() throws Exception {
        mockMvc.perform(get("/api/colors/info")
                        .param("hex", "FF00FF"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.compatible", hasSize(3)));
    }
}
