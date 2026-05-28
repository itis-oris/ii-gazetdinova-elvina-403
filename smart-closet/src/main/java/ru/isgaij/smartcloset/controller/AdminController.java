package ru.isgaij.smartcloset.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.isgaij.smartcloset.repository.UserRepository;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserRepository userRepository;

    public AdminController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping
    public String adminPage(Model model) {
        model.addAttribute("users", userRepository.findAll());
        return "admin/index";
    }
}
