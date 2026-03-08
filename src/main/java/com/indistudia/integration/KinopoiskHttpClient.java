package com.indistudia.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.indistudia.config.AppConfig;
import com.indistudia.integration.dto.FilmSearchResponse;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Optional;

@Slf4j
public class KinopoiskHttpClient {
    private final Object lock = new Object();
    private long lastRequestAtMillis;
    private static final long MIN_REQUREST_INTERVAL_MS = 50L;

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final AppConfig config;

    public KinopoiskHttpClient(AppConfig config, ObjectMapper objectMapper) {
        this.config = config;
        this.objectMapper = objectMapper;

        this.httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .connectTimeout(Duration.ofSeconds(5))
                .build();
    }

    public Optional<FilmSearchResponse> search(String query, int page) {
        if (query == null || query.isBlank()) return Optional.empty();

        String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
        String path = "api/v2.1/films/search-by-keyword?keyword=" + encodedQuery + "&page=" + Math.max(page, 1);

        return Optional.ofNullable(executeJsonGet(path, FilmSearchResponse.class));
    }

    private <T> T executeJsonGet(String pathWithQuery, Class<T> responseType) {
        HttpRequest request = HttpRequest.newBuilder()
                .timeout(Duration.ofSeconds(10))
                .header("Content-Type", "application/json")
                .headers("X-API-KEY", config.getKinopoiskConfig().kinopoiskApiKey())
                .uri(URI.create(config.getKinopoiskConfig().kinopoiskBaseUrl() + pathWithQuery))
                .build();

        throttle();
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

            int statusCode = response.statusCode();

            if (statusCode < 200 || statusCode >= 300) {
                log.warn("Something went wrong", response.body());
                return null;
            }

            return objectMapper.readValue(response.body(), responseType);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void throttle() {
        synchronized (lock) {
            long now = System.currentTimeMillis();
            long waitMillis = MIN_REQUREST_INTERVAL_MS - (now - lastRequestAtMillis);
            if (waitMillis > 0) {
                try {
                    Thread.sleep(waitMillis);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("RPS exceeded");
                }
            }
            lastRequestAtMillis = System.currentTimeMillis();
        }
    }
}
