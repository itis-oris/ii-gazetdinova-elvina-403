package ru.isgaij.smartcloset.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.isgaij.smartcloset.dto.ItemForm;
import ru.isgaij.smartcloset.entity.Item;
import ru.isgaij.smartcloset.entity.User;
import ru.isgaij.smartcloset.exception.ResourceNotFoundException;
import ru.isgaij.smartcloset.repository.BrandRepository;
import ru.isgaij.smartcloset.repository.CategoryRepository;
import ru.isgaij.smartcloset.repository.UserRepository;
import ru.isgaij.smartcloset.service.FileStorageService;
import ru.isgaij.smartcloset.service.ItemService;

import java.io.IOException;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/items")
public class ItemController {

    private static final Logger log = LoggerFactory.getLogger(ItemController.class);

    private final ItemService itemService;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;

    public ItemController(ItemService itemService,
                          CategoryRepository categoryRepository,
                          BrandRepository brandRepository,
                          UserRepository userRepository,
                          FileStorageService fileStorageService) {
        this.itemService = itemService;
        this.categoryRepository = categoryRepository;
        this.brandRepository = brandRepository;
        this.userRepository = userRepository;
        this.fileStorageService = fileStorageService;
    }

    private User currentUser(Principal principal) {
        return userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден"));
    }

    @GetMapping
    public String list(Model model, Principal principal,
                       @RequestParam(required = false) String season,
                       @RequestParam(required = false) String name,
                       @RequestParam(required = false) Long categoryId) {
        User user = currentUser(principal);

        List<Item> items = itemService.searchItems(user, season, name, categoryId);

        List<Long> expensiveIds = itemService.findItemsAboveAveragePrice(user.getId())
                .stream().map(Item::getId).toList();

        Map<String, List<Item>> matchingItems = new HashMap<>();
        for (Item item : items) {
            matchingItems.put(String.valueOf(item.getId()), itemService.findMatchingItems(item));
        }

        model.addAttribute("items", items);
        model.addAttribute("matchingItems", matchingItems);
        model.addAttribute("expensiveIds", expensiveIds);
        model.addAttribute("selectedSeason", season != null ? season : "");
        model.addAttribute("selectedName", name != null ? name : "");
        model.addAttribute("selectedCategoryId", categoryId);
        model.addAttribute("categories", categoryRepository.findAll());
        return "item/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("form", new ItemForm());
        addCatalogs(model);
        return "item/form";
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String create(@Valid @ModelAttribute("form") ItemForm form,
                         BindingResult bindingResult,
                         Principal principal,
                         Model model) {
        if (bindingResult.hasErrors()) {
            addCatalogs(model);
            return "item/form";
        }
        User user = currentUser(principal);
        Item item = itemService.fromForm(form, user);

        MultipartFile photo = form.getPhoto();
        if (photo != null && !photo.isEmpty()) {
            try {
                item.setImageUrl(fileStorageService.save(photo));
            } catch (IOException e) {
                log.warn("Не удалось сохранить фото: {}", e.getMessage());
                model.addAttribute("photoError", "Не удалось загрузить фото, попробуйте позже");
                addCatalogs(model);
                return "item/form";
            }
        }

        itemService.save(item);
        return "redirect:/items";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model, Principal principal) {
        User user = currentUser(principal);
        Item item = itemService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Вещь не найдена"));
        verifyOwnership(item, user);

        model.addAttribute("form", itemService.toForm(item));
        model.addAttribute("item", item);
        addCatalogs(model);
        return "item/form";
    }

    @PostMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("form") ItemForm form,
                         BindingResult bindingResult,
                         Principal principal,
                         Model model) {
        User user = currentUser(principal);
        Item existing = itemService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Вещь не найдена"));
        verifyOwnership(existing, user);

        if (bindingResult.hasErrors()) {
            model.addAttribute("item", existing);
            addCatalogs(model);
            return "item/form";
        }

        form.setId(id);
        Item item = itemService.fromForm(form, user);

        MultipartFile photo = form.getPhoto();
        if (photo != null && !photo.isEmpty()) {
            try {
                if (existing.getImageUrl() != null) {
                    fileStorageService.delete(existing.getImageUrl());
                }
                item.setImageUrl(fileStorageService.save(photo));
            } catch (IOException e) {
                log.warn("Не удалось обновить фото: {}", e.getMessage());
                item.setImageUrl(existing.getImageUrl());
            }
        } else {
            item.setImageUrl(existing.getImageUrl());
        }

        itemService.save(item);
        return "redirect:/items";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, Principal principal) {
        User user = currentUser(principal);
        Item item = itemService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Вещь не найдена"));
        verifyOwnership(item, user);

        if (item.getImageUrl() != null) {
            fileStorageService.delete(item.getImageUrl());
        }
        itemService.deleteById(id);
        return "redirect:/items";
    }

    private void verifyOwnership(Item item, User user) {
        if (item.getUser() == null || !item.getUser().getId().equals(user.getId())) {
            throw new ResourceNotFoundException("Вещь не найдена");
        }
    }

    private void addCatalogs(Model model) {
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("brands", brandRepository.findAll());
    }
}
