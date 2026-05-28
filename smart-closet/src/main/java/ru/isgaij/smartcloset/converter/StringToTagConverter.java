package ru.isgaij.smartcloset.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.isgaij.smartcloset.entity.Tag;
import ru.isgaij.smartcloset.repository.TagRepository;

@Component
public class StringToTagConverter implements Converter<String, Tag> {
    private TagRepository tagRepository;
    public StringToTagConverter(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @Override
    public Tag convert(String source) {
        if (source == null) return null;
        return tagRepository.findById(Long.parseLong(source)).orElseThrow(() -> new IllegalArgumentException("Тег не найден: " + source));
    }
}