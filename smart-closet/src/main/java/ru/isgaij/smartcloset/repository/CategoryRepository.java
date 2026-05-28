package ru.isgaij.smartcloset.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.isgaij.smartcloset.entity.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
}
