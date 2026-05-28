package ru.isgaij.smartcloset.security;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.isgaij.smartcloset.entity.User;
import ru.isgaij.smartcloset.repository.UserRepository;

import java.util.List;

@Service
public class CustomUserDetailService implements UserDetailsService {
    private final UserRepository userRepository;

    public CustomUserDetailService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new UsernameNotFoundException("User not found"));

    List<SimpleGrantedAuthority> authorities = user.getRoles().stream()
            .map(role -> new SimpleGrantedAuthority(role.getName()))
            .toList();

    return new org.springframework.security.core.userdetails.User(
            user.getUsername(),
            user.getPassword(),
            authorities
    );
    }

}