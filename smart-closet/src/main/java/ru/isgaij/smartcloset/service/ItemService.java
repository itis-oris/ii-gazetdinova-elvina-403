package ru.isgaij.smartcloset.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.isgaij.smartcloset.client.ColorServiceClient;
import ru.isgaij.smartcloset.dto.ItemForm;
import ru.isgaij.smartcloset.entity.Item;
import ru.isgaij.smartcloset.entity.User;
import ru.isgaij.smartcloset.exception.ResourceNotFoundException;
import ru.isgaij.smartcloset.messaging.ColorRecommendationProducer;
import ru.isgaij.smartcloset.messaging.ColorRecommendationRequest;
import ru.isgaij.smartcloset.repository.BrandRepository;
import ru.isgaij.smartcloset.repository.CategoryRepository;
import ru.isgaij.smartcloset.repository.ItemRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ItemService {

    private static final Logger log = LoggerFactory.getLogger(ItemService.class);

    private final ItemRepository itemRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final ColorServiceClient colorServiceClient;
    private final ColorRecommendationProducer recommendationProducer;

    @PersistenceContext
    private EntityManager entityManager;

    public ItemService(ItemRepository itemRepository,
                       CategoryRepository categoryRepository,
                       BrandRepository brandRepository,
                       ColorServiceClient colorServiceClient,
                       ColorRecommendationProducer recommendationProducer) {
        this.itemRepository = itemRepository;
        this.categoryRepository = categoryRepository;
        this.brandRepository = brandRepository;
        this.colorServiceClient = colorServiceClient;
        this.recommendationProducer = recommendationProducer;
    }

    @Transactional(readOnly = true)
    public List<Item> findAll() {
        return itemRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Item> findById(Long id) {
        return itemRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Item> findAllByUser(User user) {
        return itemRepository.findAllByUser(user);
    }

    @Transactional
    public Item save(Item item) {

        if (item.getId() != null && (item.getColor() == null || item.getColor().isBlank())) {
            itemRepository.findById(item.getId()).ifPresent(existing -> {
                item.setColor(existing.getColor());
                item.setColorName(existing.getColorName());
                item.setComplementColor(existing.getComplementColor());
            });
        }

        if (item.getColor() != null && !item.getColor().isBlank()) {
            item.setColor(normalizeHex(item.getColor()));
            Map<String, Object> info = colorServiceClient.getColorInfo(item.getColor());
            if (!info.isEmpty()) {
                Object name = info.get("name");
                Object complement = info.get("complement");
                if (name != null) item.setColorName(name.toString());
                if (complement != null) item.setComplementColor(normalizeHex(complement.toString()));
            }
        }

        Item saved = itemRepository.save(item);

        if (saved.getColor() != null && !saved.getColor().isBlank()) {
            recommendationProducer.send(new ColorRecommendationRequest(
                    saved.getId(), saved.getColor(),
                    saved.getUser() != null ? saved.getUser().getId() : null
            ));
        }
        return saved;
    }

    private String normalizeHex(String hex) {
        if (hex == null || hex.isBlank()) return hex;
        String h = hex.trim();
        if (!h.startsWith("#")) h = "#" + h;
        return h.toLowerCase();
    }

    @Transactional
    public void applyColorRecommendation(Long itemId, String colorName, String complementHex) {
        itemRepository.findById(itemId).ifPresent(item -> {
            if (colorName != null && !colorName.isBlank()) item.setColorName(colorName);
            if (complementHex != null && !complementHex.isBlank()) item.setComplementColor(normalizeHex(complementHex));
            itemRepository.save(item);
            log.debug("Updated item {} with colorName={} complement={}", itemId, colorName, complementHex);
        });
    }

    @Transactional
    public void deleteById(Long id) {
        itemRepository.deleteById(id);
    }


    @Transactional(readOnly = true)
    public List<Item> findExpensiveItems(Long userId, BigDecimal minPrice) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Item> query = cb.createQuery(Item.class);
        Root<Item> root = query.from(Item.class);

        query.select(root).where(
                cb.equal(root.get("user").get("id"), userId),
                cb.greaterThan(root.get("price"), minPrice)
        );
        return entityManager.createQuery(query).getResultList();
    }

    @Transactional(readOnly = true)
    public List<Item> searchItems(User user, String season, String nameQuery, Long categoryId) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Item> cq = cb.createQuery(Item.class);
        Root<Item> root = cq.from(Item.class);

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(root.get("user"), user));

        if (season != null && !season.isBlank()) {
            predicates.add(cb.equal(root.get("season"), season));
        }
        if (nameQuery != null && !nameQuery.isBlank()) {
            predicates.add(cb.like(cb.lower(root.get("name")), "%" + nameQuery.toLowerCase() + "%"));
        }
        if (categoryId != null) {
            Join<Object, Object> categoryJoin = root.join("category");
            predicates.add(cb.equal(categoryJoin.get("id"), categoryId));
        }

        cq.select(root).where(predicates.toArray(new Predicate[0]));
        return entityManager.createQuery(cq).getResultList();
    }

    @Transactional(readOnly = true)
    public List<Item> findByUserIdAndSeason(Long userId, String season) {
        return itemRepository.findByUserIdAndSeason(userId, season);
    }

    @Transactional(readOnly = true)
    public List<Item> findItemsAboveAveragePrice(Long userId) {
        return itemRepository.findItemsAboveAveragePrice(userId);
    }

    @Transactional(readOnly = true)
    public List<Item> findMatchingItems(Item item) {
        if (item.getComplementColor() == null || item.getComplementColor().isBlank() || item.getUser() == null) {
            return List.of();
        }
        String target = normalizeHex(item.getComplementColor());
        return itemRepository.findByUserAndColor(item.getUser(), target);
    }

    @Transactional(readOnly = true)
    public ItemForm toForm(Item item) {
        ItemForm form = new ItemForm();
        form.setId(item.getId());
        form.setName(item.getName());
        form.setSize(item.getSize());
        form.setSeason(item.getSeason());
        form.setPrice(item.getPrice());
        form.setColor(item.getColor());
        if (item.getCategory() != null) form.setCategoryId(item.getCategory().getId());
        if (item.getBrand() != null)    form.setBrandId(item.getBrand().getId());
        return form;
    }

    @Transactional(readOnly = true)
    public Item fromForm(ItemForm form, User user) {
        Item item = form.getId() != null
                ? itemRepository.findById(form.getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Вещь не найдена"))
                : new Item();

        item.setName(form.getName());
        item.setSize(form.getSize());
        item.setSeason(form.getSeason());
        item.setPrice(form.getPrice());
        item.setColor(form.getColor());
        item.setUser(user);

        item.setCategory(form.getCategoryId() != null
                ? categoryRepository.findById(form.getCategoryId())
                        .orElseThrow(() -> new ResourceNotFoundException("Категория не найдена"))
                : null);

        item.setBrand(form.getBrandId() != null
                ? brandRepository.findById(form.getBrandId())
                        .orElseThrow(() -> new ResourceNotFoundException("Бренд не найден"))
                : null);

        return item;
    }
}
