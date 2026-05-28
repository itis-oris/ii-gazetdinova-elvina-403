package ru.isgaij.colorservice.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class ColorService {

    private static final Map<String, String> COLOR_TABLE = new LinkedHashMap<>();

    static {
        COLOR_TABLE.put("FF0000", "Красный");
        COLOR_TABLE.put("00FF00", "Зелёный");
        COLOR_TABLE.put("0000FF", "Синий");
        COLOR_TABLE.put("FFFFFF", "Белый");
        COLOR_TABLE.put("000000", "Чёрный");
        COLOR_TABLE.put("FFFF00", "Жёлтый");
        COLOR_TABLE.put("FF00FF", "Маджента");
        COLOR_TABLE.put("00FFFF", "Циан");
        COLOR_TABLE.put("FFA500", "Оранжевый");
        COLOR_TABLE.put("800080", "Фиолетовый");
        COLOR_TABLE.put("A52A2A", "Коричневый");
        COLOR_TABLE.put("808080", "Серый");
        COLOR_TABLE.put("FFC0CB", "Розовый");
    }

    public String resolveName(String hex) {
        String normalized = normalize(hex);

        if (COLOR_TABLE.containsKey(normalized)) {
            return COLOR_TABLE.get(normalized);
        }

        int[] rgb = hexToRgb(normalized);
        String closestName = "Неизвестный";
        double minDistance = Double.MAX_VALUE;

        for (Map.Entry<String, String> entry : COLOR_TABLE.entrySet()) {
            int[] refRgb = hexToRgb(entry.getKey());
            double distance = rgbDistance(rgb, refRgb);
            if (distance < minDistance) {
                minDistance = distance;
                closestName = entry.getValue();
            }
        }

        return closestName;
    }

    public String complement(String hex) {
        String normalized = normalize(hex);
        int[] rgb = hexToRgb(normalized);
        int[] comp = {255 - rgb[0], 255 - rgb[1], 255 - rgb[2]};
        return rgbToHex(comp);
    }

    public List<String> compatibleColors(String hex) {
        String normalized = normalize(hex);
        int[] rgb = hexToRgb(normalized);

        List<String> result = new ArrayList<>();

        result.add(complement(normalized));

        result.add(shiftHue(rgb, 120.0));

        result.add(shiftHue(rgb, 240.0));

        return result;
    }

    public String normalize(String hex) {
        if (hex == null || hex.isBlank()) {
            return "000000";
        }
        String result = hex.trim().toUpperCase();
        if (result.startsWith("#")) {
            result = result.substring(1);
        }

        if (result.length() == 3) {
            result = String.valueOf(result.charAt(0)) + result.charAt(0)
                    + result.charAt(1) + result.charAt(1)
                    + result.charAt(2) + result.charAt(2);
        }

        if (!result.matches("[0-9A-F]{6}")) {
            return "000000";
        }
        return result;
    }




    private int[] hexToRgb(String hex) {
        int r = Integer.parseInt(hex.substring(0, 2), 16);
        int g = Integer.parseInt(hex.substring(2, 4), 16);
        int b = Integer.parseInt(hex.substring(4, 6), 16);
        return new int[]{r, g, b};
    }

    private String rgbToHex(int[] rgb) {
        return String.format("%02X%02X%02X", rgb[0], rgb[1], rgb[2]);
    }

    private double rgbDistance(int[] a, int[] b) {
        int dr = a[0] - b[0];
        int dg = a[1] - b[1];
        int db = a[2] - b[2];
        return Math.sqrt((double) dr * dr + (double) dg * dg + (double) db * db);
    }

    private String shiftHue(int[] rgb, double hueShift) {

        double r = rgb[0] / 255.0;
        double g = rgb[1] / 255.0;
        double b = rgb[2] / 255.0;

        double max = Math.max(r, Math.max(g, b));
        double min = Math.min(r, Math.min(g, b));
        double delta = max - min;

        double h = 0.0;
        double s = (max == 0.0) ? 0.0 : delta / max;
        double v = max;

        if (delta != 0.0) {
            if (max == r) {
                h = 60.0 * (((g - b) / delta) % 6.0);
            } else if (max == g) {
                h = 60.0 * (((b - r) / delta) + 2.0);
            } else {
                h = 60.0 * (((r - g) / delta) + 4.0);
            }
        }
        if (h < 0.0) {
            h += 360.0;
        }

        h = (h + hueShift) % 360.0;

        double c = v * s;
        double x = c * (1.0 - Math.abs((h / 60.0) % 2.0 - 1.0));
        double m = v - c;

        double rPrime, gPrime, bPrime;
        if (h < 60.0) {
            rPrime = c; gPrime = x; bPrime = 0;
        } else if (h < 120.0) {
            rPrime = x; gPrime = c; bPrime = 0;
        } else if (h < 180.0) {
            rPrime = 0; gPrime = c; bPrime = x;
        } else if (h < 240.0) {
            rPrime = 0; gPrime = x; bPrime = c;
        } else if (h < 300.0) {
            rPrime = x; gPrime = 0; bPrime = c;
        } else {
            rPrime = c; gPrime = 0; bPrime = x;
        }

        int rOut = (int) Math.round((rPrime + m) * 255.0);
        int gOut = (int) Math.round((gPrime + m) * 255.0);
        int bOut = (int) Math.round((bPrime + m) * 255.0);

        rOut = Math.max(0, Math.min(255, rOut));
        gOut = Math.max(0, Math.min(255, gOut));
        bOut = Math.max(0, Math.min(255, bOut));

        return rgbToHex(new int[]{rOut, gOut, bOut});
    }
}
