package ru.isgaij.smartcloset.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.isgaij.smartcloset.dto.WishlistItemRequest;
import ru.isgaij.smartcloset.dto.WishlistItemResponse;
import ru.isgaij.smartcloset.entity.User;
import ru.isgaij.smartcloset.entity.WishlistItem;
import ru.isgaij.smartcloset.exception.ResourceNotFoundException;
import ru.isgaij.smartcloset.repository.UserRepository;
import ru.isgaij.smartcloset.service.WishlistItemService;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/wishlist")
@Tag(name = "Wishlist", description = "API списка желаемых покупок")
@SecurityRequirement(name = "session")
public class WishlistItemRestController {

    private final WishlistItemService wishlistItemService;
    private final UserRepository userRepository;

    public WishlistItemRestController(WishlistItemService wishlistItemService,
                                      UserRepository userRepository) {
        this.wishlistItemService = wishlistItemService;
        this.userRepository = userRepository;
    }

    private User currentUser(Principal principal) {
        return userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден"));
    }

    @GetMapping
    @Operation(summary = "Получить вишлист текущего пользователя")
    public List<WishlistItemResponse> getAll(Principal principal) {
        User user = currentUser(principal);
        return wishlistItemService.findAllByUserId(user.getId())
                .stream().map(WishlistItemResponse::from).toList();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить элемент вишлиста по id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "404", description = "Не найдено")
    })
    public WishlistItemResponse getOne(@PathVariable Long id, Principal principal) {
        User user = currentUser(principal);
        WishlistItem item = wishlistItemService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Элемент вишлиста не найден"));
        if (!item.getUser().getId().equals(user.getId())) {
            throw new ResourceNotFoundException("Элемент вишлиста не найден");
        }
        return WishlistItemResponse.from(item);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Добавить элемент в вишлист")
    public WishlistItemResponse create(@Valid @RequestBody WishlistItemRequest request,
                                       Principal principal) {
        User user = currentUser(principal);

        WishlistItem item = new WishlistItem();
        item.setName(request.getName());
        item.setPrice(request.getPrice());
        item.setUrl(request.getUrl());
        item.setNote(request.getNote());
        item.setUser(user);
        return WishlistItemResponse.from(wishlistItemService.save(item));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить элемент вишлиста")
    public WishlistItemResponse update(@PathVariable Long id,
                                       @Valid @RequestBody WishlistItemRequest request,
                                       Principal principal) {
        User user = currentUser(principal);

        WishlistItem item = wishlistItemService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Элемент вишлиста не найден"));
        if (!item.getUser().getId().equals(user.getId())) {
            throw new ResourceNotFoundException("Элемент вишлиста не найден");
        }

        item.setName(request.getName());
        item.setPrice(request.getPrice());
        item.setUrl(request.getUrl());
        item.setNote(request.getNote());

        return WishlistItemResponse.from(wishlistItemService.save(item));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Удалить элемент вишлиста")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Удалено"),
            @ApiResponse(responseCode = "404", description = "Не найдено")
    })
    public void delete(@PathVariable Long id, Principal principal) {
        User user = currentUser(principal);
        WishlistItem item = wishlistItemService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Элемент вишлиста не найден"));
        if (!item.getUser().getId().equals(user.getId())) {
            throw new ResourceNotFoundException("Элемент вишлиста не найден");
        }
        wishlistItemService.deleteById(id);
    }
}
