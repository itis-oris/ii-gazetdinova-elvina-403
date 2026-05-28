package ru.isgaij.smartcloset.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.isgaij.smartcloset.entity.Purchase;
import ru.isgaij.smartcloset.repository.PurchaseRepository;

import java.util.List;

@Service
public class PurchaseService {

    private final PurchaseRepository purchaseRepository;

    public PurchaseService(PurchaseRepository purchaseRepository) {
        this.purchaseRepository = purchaseRepository;
    }

    @Transactional(readOnly = true)
    public List<Purchase> findAllByUserId(Long userId) {
        return purchaseRepository.findByUserId(userId);
    }

    @Transactional(readOnly = true)
    public List<Object[]> totalSpentByBrand(Long userId) {
        return purchaseRepository.totalSpentByBrand(userId);
    }

    @Transactional
    public Purchase save(Purchase purchase) {
        return purchaseRepository.save(purchase);
    }
}
