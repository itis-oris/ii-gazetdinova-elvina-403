package ru.isgaij.smartcloset.controller;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.isgaij.smartcloset.dto.PurchaseForm;
import ru.isgaij.smartcloset.entity.Item;
import ru.isgaij.smartcloset.entity.Purchase;
import ru.isgaij.smartcloset.entity.User;
import ru.isgaij.smartcloset.exception.ResourceNotFoundException;
import ru.isgaij.smartcloset.service.ItemService;
import ru.isgaij.smartcloset.service.PurchaseService;
import ru.isgaij.smartcloset.service.UserService;

import java.security.Principal;

@Controller
@RequestMapping("/purchases")
public class PurchaseController {

    private final PurchaseService purchaseService;
    private final UserService userService;
    private final ItemService itemService;

    public PurchaseController(PurchaseService purchaseService,
                              UserService userService,
                              ItemService itemService) {
        this.purchaseService = purchaseService;
        this.userService = userService;
        this.itemService = itemService;
    }

    private User currentUser(Principal principal) {
        return userService.findByUsername(principal.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден"));
    }

    @GetMapping
    public String list(Model model, Principal principal) {
        User user = currentUser(principal);
        model.addAttribute("purchases",   purchaseService.findAllByUserId(user.getId()));
        model.addAttribute("spentByBrand", purchaseService.totalSpentByBrand(user.getId()));
        return "purchase/list";
    }

    @GetMapping("/new")
    public String createForm(Model model, Principal principal) {
        User user = currentUser(principal);
        model.addAttribute("form", new PurchaseForm());
        model.addAttribute("items", itemService.findAllByUser(user));
        return "purchase/form";
    }

    @PostMapping
    public String save(@Valid @ModelAttribute("form") PurchaseForm form,
                       BindingResult bindingResult,
                       Principal principal,
                       Model model) {
        User user = currentUser(principal);

        if (bindingResult.hasErrors()) {
            model.addAttribute("items", itemService.findAllByUser(user));
            return "purchase/form";
        }

        Item item = itemService.findById(form.getItemId())
                .orElseThrow(() -> new ResourceNotFoundException("Вещь не найдена"));

        if (!item.getUser().getId().equals(user.getId())) {
            throw new ResourceNotFoundException("Вещь не найдена");
        }

        Purchase purchase = new Purchase();
        purchase.setUser(user);
        purchase.setItem(item);
        purchase.setPurchaseDate(form.getPurchaseDate());
        purchase.setPrice(form.getPrice());
        purchase.setNote(form.getNote());

        purchaseService.save(purchase);
        return "redirect:/purchases";
    }
}
