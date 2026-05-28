package ru.isgaij.smartcloset.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

import java.time.Duration;

@Configuration
public class RestClientConfig {

    @Bean
    public RestClient colorServiceRestClient(@Value("${app.color-service.base-url}") String baseUrl) {
        return RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(clientHttpRequestFactory())
                .build();
    }

    private static org.springframework.http.client.SimpleClientHttpRequestFactory clientHttpRequestFactory() {
        org.springframework.http.client.SimpleClientHttpRequestFactory f =
                new org.springframework.http.client.SimpleClientHttpRequestFactory();
        f.setConnectTimeout((int) Duration.ofSeconds(3).toMillis());
        f.setReadTimeout((int) Duration.ofSeconds(5).toMillis());
        return f;
    }
}
