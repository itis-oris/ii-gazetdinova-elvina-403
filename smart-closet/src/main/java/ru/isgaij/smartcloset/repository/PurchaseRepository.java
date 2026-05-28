package ru.isgaij.smartcloset.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.isgaij.smartcloset.entity.Purchase;

import java.util.List;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, Long> {

    List<Purchase> findByUserId(Long userId);

    @Query("""
           SELECT b.name, SUM(p.price)
           FROM Purchase p JOIN p.item i JOIN i.brand b
           WHERE p.user.id = :userId
           GROUP BY b.name
           ORDER BY SUM(p.price) DESC
           """)
    List<Object[]> totalSpentByBrand(@Param("userId") Long userId);
}
