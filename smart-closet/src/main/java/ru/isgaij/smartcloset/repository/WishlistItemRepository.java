package ru.isgaij.smartcloset.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.isgaij.smartcloset.entity.WishlistItem;

import java.util.List;

@Repository
public interface WishlistItemRepository extends JpaRepository<WishlistItem, Long> {
    List<WishlistItem> findByUserId(Long userId);
}
