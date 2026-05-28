package ru.isgaij.smartcloset.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.isgaij.smartcloset.entity.Brand;
import ru.isgaij.smartcloset.repository.BrandRepository;

@Component
public class StringToBrandConverter implements Converter<String, Brand> {
    private BrandRepository brandRepository;
    public StringToBrandConverter(BrandRepository brandRepository) {
        this.brandRepository = brandRepository;
    }

    @Override
    public Brand convert(String source) {
        if (source == null || source.isBlank())
            return null;
        return brandRepository.findById(Long.parseLong(source)).orElseThrow(() -> new IllegalArgumentException("Бренд не найден: " + source));
    }
}