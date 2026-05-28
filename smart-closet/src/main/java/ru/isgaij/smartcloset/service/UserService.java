package ru.isgaij.smartcloset.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.isgaij.smartcloset.dto.RegisterForm;
import ru.isgaij.smartcloset.entity.Role;
import ru.isgaij.smartcloset.entity.User;
import ru.isgaij.smartcloset.exception.UserAlreadyExistsException;
import ru.isgaij.smartcloset.repository.RoleRepository;
import ru.isgaij.smartcloset.repository.UserRepository;

import java.util.Optional;
import java.util.Set;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void register(RegisterForm form) {
        if (userRepository.existsByUsername(form.getUsername())) {
            throw new UserAlreadyExistsException("Имя пользователя уже занято");
        }
        if (userRepository.existsByEmail(form.getEmail())) {
            throw new UserAlreadyExistsException("Email уже зарегистрирован");
        }

        Role role = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new IllegalStateException("Роль ROLE_USER не найдена в БД"));

        User user = new User();
        user.setUsername(form.getUsername());
        user.setEmail(form.getEmail());
        user.setPassword(passwordEncoder.encode(form.getPassword()));
        user.setRoles(Set.of(role));

        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}
