package ru.isgaij.smartcloset.client;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class ColorServiceClient {

    private static final Logger log = LoggerFactory.getLogger(ColorServiceClient.class);

    private final RestClient colorServiceRestClient;

    @Cacheable(cacheNames = "colorInfo", key = "#root.target.normalize(#hex)")
    public Map<String, Object> getColorInfo(String hex) {
        String normalized = normalize(hex);
        log.debug("[HTTP] GET color-service /api/colors/info?hex={}", normalized);

        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> body = colorServiceRestClient.get()
                    .uri(uri -> uri.path("/api/colors/info").queryParam("hex", normalized).build())
                    .retrieve()
                    .body(Map.class);

            return body != null ? body : Map.of();
        } catch (RestClientException e) {

            log.warn("color-service недоступен: {}", e.getMessage());
            return Map.of();
        }
    }

    public String normalize(String hex) {
        if (hex == null) return "";
        return hex.replace("#", "").toUpperCase().trim();
    }
}
