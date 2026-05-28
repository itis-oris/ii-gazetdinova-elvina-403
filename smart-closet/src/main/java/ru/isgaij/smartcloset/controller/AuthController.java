package ru.isgaij.smartcloset.controller;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import ru.isgaij.smartcloset.dto.RegisterForm;
import ru.isgaij.smartcloset.exception.UserAlreadyExistsException;
import ru.isgaij.smartcloset.service.UserService;

@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String loginPage() {
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        if (!model.containsAttribute("form")) {
            model.addAttribute("form", new RegisterForm());
        }
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("form") RegisterForm form,
                           BindingResult bindingResult,
                           Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("errors", bindingResult);
            return "auth/register";
        }

        try {
            userService.register(form);
        } catch (UserAlreadyExistsException ex) {
            model.addAttribute("form", form);
            model.addAttribute("globalError", ex.getMessage());
            return "auth/register";
        }
        return "redirect:/login?registered";
    }
}
