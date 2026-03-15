package com.indistudia.service;

import com.indistudia.cache.CacheProvider;
import com.indistudia.domain.Media;
import com.indistudia.integration.KinopoiskHttpClient;
import com.indistudia.integration.dto.FilmSearchResponse;
import com.indistudia.mappers.MediaMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
public class FilmsProxy {
    private final MediaService mediaService;
    private final KinopoiskHttpClient httpClient;

    public List<Media> findFilms(String query) {
        List<Media> media = CacheProvider.get(query);

        if (media == null) {
            media = mediaService.findByQuery(query);
            CacheProvider.set(query, media);
        }

        if (!media.isEmpty())
            return media;

        Optional<FilmSearchResponse> filmSearchResponse = httpClient.search(query, 1);
        var films = MediaMapper.convertToDomain(filmSearchResponse);
        saveFilms(films);
        return films;
    }

    private void saveFilms(List<Media> media) {
        try {
            mediaService.saveAll(media);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
