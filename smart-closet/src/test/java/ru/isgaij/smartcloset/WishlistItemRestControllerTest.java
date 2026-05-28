package ru.isgaij.smartcloset;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.isgaij.smartcloset.controller.WishlistItemRestController;
import ru.isgaij.smartcloset.dto.WishlistItemRequest;
import ru.isgaij.smartcloset.entity.User;
import ru.isgaij.smartcloset.entity.WishlistItem;
import ru.isgaij.smartcloset.exception.RestExceptionHandler;
import ru.isgaij.smartcloset.repository.UserRepository;
import ru.isgaij.smartcloset.service.WishlistItemService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WishlistItemRestController.class)
@Import(RestExceptionHandler.class)
class WishlistItemRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private WishlistItemService wishlistItemService;

    @MockitoBean
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("test");
        when(userRepository.findByUsername("test")).thenReturn(Optional.of(testUser));
    }

    @Test
    void getAll_shouldReturn200_whenAuthenticated() throws Exception {
        when(wishlistItemService.findAllByUserId(1L)).thenReturn(List.of());

        mockMvc.perform(get("/api/wishlist").with(user("test").roles("USER")))
                .andExpect(status().isOk());
    }

    @Test
    void getAll_shouldReturn401_whenAnonymous() throws Exception {

        mockMvc.perform(get("/api/wishlist"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    void create_shouldReturn400_whenNameMissing() throws Exception {

        WishlistItemRequest bad = new WishlistItemRequest();
        bad.setName("");
        bad.setPrice(new BigDecimal("10.00"));

        mockMvc.perform(post("/api/wishlist")
                        .with(user("test").roles("USER"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bad)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors").exists());
    }

    @Test
    void create_shouldReturn201_whenValid() throws Exception {
        WishlistItemRequest req = new WishlistItemRequest();
        req.setName("Кроссовки");
        req.setPrice(new BigDecimal("5990.00"));
        req.setUrl("https://example.com/item");
        req.setNote("На лето");

        WishlistItem saved = new WishlistItem();
        saved.setId(42L);
        saved.setName(req.getName());
        saved.setPrice(req.getPrice());
        saved.setUrl(req.getUrl());
        saved.setUser(testUser);

        when(wishlistItemService.save(any(WishlistItem.class))).thenReturn(saved);

        mockMvc.perform(post("/api/wishlist")
                        .with(user("test").roles("USER"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(42))
                .andExpect(jsonPath("$.name").value("Кроссовки"));
    }

    @Test
    void create_shouldReturn403_whenNoCsrfToken() throws Exception {
        WishlistItemRequest req = new WishlistItemRequest();
        req.setName("Шапка");
        req.setPrice(new BigDecimal("500.00"));

        mockMvc.perform(post("/api/wishlist")
                        .with(user("test").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isForbidden());
    }

    @Test
    void update_shouldReturn404_whenItemNotFound() throws Exception {
        when(wishlistItemService.findById(999L)).thenReturn(Optional.empty());

        WishlistItemRequest req = new WishlistItemRequest();
        req.setName("Сумка");

        mockMvc.perform(put("/api/wishlist/999")
                        .with(user("test").roles("USER"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isNotFound());
    }

    @Test
    void update_shouldReturn404_whenItemBelongsToOtherUser() throws Exception {

        User other = new User();
        other.setId(2L);

        WishlistItem foreign = new WishlistItem();
        foreign.setId(7L);
        foreign.setUser(other);

        when(wishlistItemService.findById(7L)).thenReturn(Optional.of(foreign));

        WishlistItemRequest req = new WishlistItemRequest();
        req.setName("Чужая шапка");

        mockMvc.perform(put("/api/wishlist/7")
                        .with(user("test").roles("USER"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isNotFound());
    }

    @Test
    void delete_shouldReturn204_whenOk() throws Exception {
        WishlistItem own = new WishlistItem();
        own.setId(5L);
        own.setUser(testUser);
        when(wishlistItemService.findById(5L)).thenReturn(Optional.of(own));

        mockMvc.perform(delete("/api/wishlist/5")
                        .with(user("test").roles("USER"))
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(wishlistItemService).deleteById(5L);
    }

    @Test
    void delete_shouldReturn404_whenItemNotFound() throws Exception {
        when(wishlistItemService.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/wishlist/99")
                        .with(user("test").roles("USER"))
                        .with(csrf()))
                .andExpect(status().isNotFound());
    }
}
