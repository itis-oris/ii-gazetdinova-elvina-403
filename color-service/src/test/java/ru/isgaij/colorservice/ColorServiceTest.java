package ru.isgaij.colorservice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.isgaij.colorservice.service.ColorService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Тесты ColorService")
class ColorServiceTest {

    private ColorService colorService;

    @BeforeEach
    void setUp() {
        colorService = new ColorService();
    }



    @Test
    @DisplayName("resolveName: FF0000 → Красный (точное совпадение)")
    void resolveName_exactMatch_red() {
        assertThat(colorService.resolveName("FF0000")).isEqualTo("Красный");
    }

    @Test
    @DisplayName("resolveName: 0000FF → Синий (точное совпадение)")
    void resolveName_exactMatch_blue() {
        assertThat(colorService.resolveName("0000FF")).isEqualTo("Синий");
    }

    @Test
    @DisplayName("resolveName: FFFFFF → Белый (точное совпадение)")
    void resolveName_exactMatch_white() {
        assertThat(colorService.resolveName("FFFFFF")).isEqualTo("Белый");
    }

    @Test
    @DisplayName("resolveName: 000000 → Чёрный (точное совпадение)")
    void resolveName_exactMatch_black() {
        assertThat(colorService.resolveName("000000")).isEqualTo("Чёрный");
    }

    @Test
    @DisplayName("resolveName: FFFF00 → Жёлтый (точное совпадение)")
    void resolveName_exactMatch_yellow() {
        assertThat(colorService.resolveName("FFFF00")).isEqualTo("Жёлтый");
    }

    @Test
    @DisplayName("resolveName: 808080 → Серый (точное совпадение)")
    void resolveName_exactMatch_gray() {
        assertThat(colorService.resolveName("808080")).isEqualTo("Серый");
    }



    @Test
    @DisplayName("resolveName: FE0000 ближайший → Красный (евклидово расстояние)")
    void resolveName_nearestNeighbor_nearRed() {

        assertThat(colorService.resolveName("FE0000")).isEqualTo("Красный");
    }

    @Test
    @DisplayName("resolveName: FEFEFE ближайший → Белый (евклидово расстояние)")
    void resolveName_nearestNeighbor_nearWhite() {

        assertThat(colorService.resolveName("FEFEFE")).isEqualTo("Белый");
    }

    @Test
    @DisplayName("resolveName: 010101 ближайший → Чёрный (евклидово расстояние)")
    void resolveName_nearestNeighbor_nearBlack() {

        assertThat(colorService.resolveName("010101")).isEqualTo("Чёрный");
    }

    @Test
    @DisplayName("resolveName: null → определяется как цвет 000000 (Чёрный)")
    void resolveName_nullHex_returnsBlack() {

        assertThat(colorService.resolveName(null)).isEqualTo("Чёрный");
    }



    @Test
    @DisplayName("complement: FFFFFF → 000000 (белый → чёрный)")
    void complement_white_returnsBlack() {
        assertThat(colorService.complement("FFFFFF")).isEqualTo("000000");
    }

    @Test
    @DisplayName("complement: 000000 → FFFFFF (чёрный → белый)")
    void complement_black_returnsWhite() {
        assertThat(colorService.complement("000000")).isEqualTo("FFFFFF");
    }

    @Test
    @DisplayName("complement: FF0000 → 00FFFF (красный → циан)")
    void complement_red_returnsCyan() {
        assertThat(colorService.complement("FF0000")).isEqualTo("00FFFF");
    }

    @Test
    @DisplayName("complement: 00FF00 → FF00FF (зелёный → маджента)")
    void complement_green_returnsMagenta() {
        assertThat(colorService.complement("00FF00")).isEqualTo("FF00FF");
    }

    @Test
    @DisplayName("complement: 0000FF → FFFF00 (синий → жёлтый)")
    void complement_blue_returnsYellow() {
        assertThat(colorService.complement("0000FF")).isEqualTo("FFFF00");
    }



    @Test
    @DisplayName("normalize: строка с # → без #, uppercase")
    void normalize_withHash_removesHash() {
        assertThat(colorService.normalize("#ff0000")).isEqualTo("FF0000");
    }

    @Test
    @DisplayName("normalize: строка без # в нижнем регистре → uppercase")
    void normalize_lowerCase_returnsUpperCase() {
        assertThat(colorService.normalize("ff0000")).isEqualTo("FF0000");
    }

    @Test
    @DisplayName("normalize: пробелы удаляются")
    void normalize_withSpaces_trimsSpaces() {
        assertThat(colorService.normalize("  FF0000  ")).isEqualTo("FF0000");
    }

    @Test
    @DisplayName("normalize: null → 000000")
    void normalize_null_returnsBlack() {
        assertThat(colorService.normalize(null)).isEqualTo("000000");
    }

    @Test
    @DisplayName("normalize: пустая строка → 000000")
    void normalize_empty_returnsBlack() {
        assertThat(colorService.normalize("")).isEqualTo("000000");
    }

    @Test
    @DisplayName("normalize: только пробелы → 000000")
    void normalize_blank_returnsBlack() {
        assertThat(colorService.normalize("   ")).isEqualTo("000000");
    }

    @Test
    @DisplayName("normalize: короткая запись #RGB → #RRGGBB")
    void normalize_shortForm_expandsToFullForm() {
        assertThat(colorService.normalize("#F00")).isEqualTo("FF0000");
    }



    @Test
    @DisplayName("compatibleColors: возвращает список из 3 элементов")
    void compatibleColors_returnsThreeElements() {
        List<String> colors = colorService.compatibleColors("FF0000");
        assertThat(colors).hasSize(3);
    }

    @Test
    @DisplayName("compatibleColors: первый элемент = комплементарный цвет")
    void compatibleColors_firstIsComplement() {
        List<String> colors = colorService.compatibleColors("FF0000");

        assertThat(colors.get(0)).isEqualTo("00FFFF");
    }

    @Test
    @DisplayName("compatibleColors: все элементы в формате 6-символьного HEX")
    void compatibleColors_allElementsAreValidHex() {
        List<String> colors = colorService.compatibleColors("FF0000");
        for (String color : colors) {
            assertThat(color).matches("[0-9A-F]{6}");
        }
    }
}
