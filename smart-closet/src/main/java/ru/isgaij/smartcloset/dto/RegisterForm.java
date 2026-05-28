package ru.isgaij.smartcloset.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RegisterForm {

    @NotBlank(message = "Введите имя пользователя")
    @Size(min = 2, max = 50, message = "Имя пользователя должно содержать от 2 до 50 символов")
    @Pattern(regexp = "^[a-zA-Z0-9_.-]+$",
             message = "Допустимы только латинские буквы, цифры, ._-")
    private String username;

    @NotBlank(message = "Введите email")
    @Email(message = "Введите корректный email")
    @Size(max = 150)
    private String email;

    @NotBlank(message = "Введите пароль")
    @Size(min = 6, max = 100, message = "Пароль должен содержать минимум 6 символов")
    private String password;
}
