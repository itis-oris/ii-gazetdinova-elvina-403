package ru.isgaij.smartcloset.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.isgaij.smartcloset.entity.Role;

import java.util.Optional;

@Repository
public interface RoleRepository  extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);
}
