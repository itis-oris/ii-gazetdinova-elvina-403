package ru.isgaij.smartcloset.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.isgaij.smartcloset.entity.WishlistItem;
import ru.isgaij.smartcloset.repository.WishlistItemRepository;

import java.util.List;
import java.util.Optional;

@Service
public class WishlistItemService {

    private final WishlistItemRepository wishlistItemRepository;

    public WishlistItemService(WishlistItemRepository wishlistItemRepository) {
        this.wishlistItemRepository = wishlistItemRepository;
    }

    @Transactional(readOnly = true)
    public List<WishlistItem> findAllByUserId(Long userId) {
        return wishlistItemRepository.findByUserId(userId);
    }

    @Transactional
    public WishlistItem save(WishlistItem item) {
        return wishlistItemRepository.save(item);
    }

    @Transactional
    public void deleteById(Long id) {
        wishlistItemRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Optional<WishlistItem> findById(Long id) {
        return wishlistItemRepository.findById(id);
    }
}
