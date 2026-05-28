package ru.isgaij.smartcloset.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.isgaij.smartcloset.entity.Brand;

@Repository
public interface BrandRepository extends JpaRepository<Brand, Long> {
}
