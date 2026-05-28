package ru.isgaij.smartcloset.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.isgaij.smartcloset.entity.Category;
import ru.isgaij.smartcloset.repository.CategoryRepository;

@Component
public class StringToCategoryConverter implements Converter<String, Category> {
    private CategoryRepository categoryRepository;

    public StringToCategoryConverter(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public Category convert(String source) {
        if (source == null || source.isBlank()) {
            return null;
        }
        return categoryRepository.findById(Long.parseLong(source)).orElseThrow(() -> new IllegalArgumentException("Категория не найдена: " + source));

    }
}
