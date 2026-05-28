package ru.isgaij.smartcloset;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.isgaij.smartcloset.client.ColorServiceClient;
import ru.isgaij.smartcloset.entity.Item;
import ru.isgaij.smartcloset.entity.User;
import ru.isgaij.smartcloset.messaging.ColorRecommendationProducer;
import ru.isgaij.smartcloset.messaging.ColorRecommendationRequest;
import ru.isgaij.smartcloset.repository.BrandRepository;
import ru.isgaij.smartcloset.repository.CategoryRepository;
import ru.isgaij.smartcloset.repository.ItemRepository;
import ru.isgaij.smartcloset.service.ItemService;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemServiceUnitTest {

    @Mock private ItemRepository itemRepository;
    @Mock private CategoryRepository categoryRepository;
    @Mock private BrandRepository brandRepository;
    @Mock private ColorServiceClient colorServiceClient;
    @Mock private ColorRecommendationProducer producer;

    @InjectMocks private ItemService itemService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(10L);
    }

    @Test
    void save_shouldCallColorServiceAndKafka_whenColorPresent() {
        Item input = new Item();
        input.setName("Куртка");
        input.setColor("FF0000");
        input.setUser(user);

        when(colorServiceClient.getColorInfo("FF0000"))
                .thenReturn(Map.of("name", "Красный", "complement", "00FFFF"));

        when(itemRepository.save(any(Item.class))).thenAnswer(invocation -> {
            Item arg = invocation.getArgument(0);
            arg.setId(123L);
            return arg;
        });

        Item saved = itemService.save(input);

        verify(colorServiceClient, times(1)).getColorInfo("FF0000");

        assertThat(saved.getColorName()).isEqualTo("Красный");
        assertThat(saved.getComplementColor()).isEqualTo("00FFFF");

        ColorRecommendationRequest expected =
                new ColorRecommendationRequest(123L, "FF0000", 10L);
        verify(producer, times(1)).send(eq(expected));
    }

    @Test
    void save_shouldNotCallColorService_whenColorBlank() {
        Item input = new Item();
        input.setName("Носки");
        input.setColor("");
        input.setUser(user);

        when(itemRepository.save(any(Item.class))).thenAnswer(inv -> inv.getArgument(0));

        itemService.save(input);

        verify(colorServiceClient, never()).getColorInfo(any());
        verify(producer, never()).send(any());
    }

    @Test
    void applyColorRecommendation_shouldUpdateFields_whenItemExists() {
        Item existing = new Item();
        existing.setId(55L);
        when(itemRepository.findById(55L)).thenReturn(Optional.of(existing));

        itemService.applyColorRecommendation(55L, "Зелёный", "FF00FF");

        assertThat(existing.getColorName()).isEqualTo("Зелёный");
        assertThat(existing.getComplementColor()).isEqualTo("FF00FF");
        verify(itemRepository).save(existing);
    }

    @Test
    void applyColorRecommendation_shouldDoNothing_whenItemMissing() {
        when(itemRepository.findById(999L)).thenReturn(Optional.empty());

        itemService.applyColorRecommendation(999L, "Жёлтый", "0000FF");

        verify(itemRepository, never()).save(any());
    }
}
