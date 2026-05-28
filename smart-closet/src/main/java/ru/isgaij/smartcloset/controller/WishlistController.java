package ru.isgaij.smartcloset.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.isgaij.smartcloset.client.ColorServiceClient;

import java.util.Map;

@Controller
@RequestMapping("/wishlist")
public class WishlistController {

    private final ColorServiceClient colorServiceClient;

    public WishlistController(ColorServiceClient colorServiceClient) {
        this.colorServiceClient = colorServiceClient;
    }

    @GetMapping
    public String wishlist(@RequestParam(defaultValue = "FFFFFF") String hex, Model model) {
        Map<String, Object> info = colorServiceClient.getColorInfo(hex);
        model.addAttribute("colorInfo", info);
        model.addAttribute("hex", hex);
        return "wishlist/index";
    }
}
