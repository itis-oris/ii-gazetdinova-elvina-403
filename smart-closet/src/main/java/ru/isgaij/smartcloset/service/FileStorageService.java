package ru.isgaij.smartcloset.service;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileStorageService {

    private static final Logger log = LoggerFactory.getLogger(FileStorageService.class);

    @Value("${app.upload-dir:uploads}")
    private String uploadDirValue;

    private Path uploadDir;

    @PostConstruct
    public void init() {
        try {
            uploadDir = Paths.get(uploadDirValue).toAbsolutePath().normalize();
            Files.createDirectories(uploadDir);
            log.info("Upload directory ready: {}", uploadDir);
        } catch (IOException e) {
            throw new RuntimeException("Не удалось создать директорию для загрузок", e);
        }
    }

    public String save(MultipartFile file) throws IOException {
        String original = StringUtils.cleanPath(
                file.getOriginalFilename() != null ? file.getOriginalFilename() : "photo"
        );
        String extension = "";
        int dot = original.lastIndexOf('.');
        if (dot >= 0) {
            extension = original.substring(dot).toLowerCase();
        }
        String filename = UUID.randomUUID() + extension;

        Path target = uploadDir.resolve(filename);
        Files.copy(file.getInputStream(), target);
        log.debug("Saved upload: {}", target);

        return "/uploads/" + filename;
    }

    public void delete(String imageUrl) {
        if (imageUrl == null || !imageUrl.startsWith("/uploads/")) return;
        String filename = imageUrl.substring("/uploads/".length());
        try {
            Files.deleteIfExists(uploadDir.resolve(filename));
        } catch (IOException e) {
            log.warn("Не удалось удалить файл {}: {}", filename, e.getMessage());
        }
    }
}
