package com.indistudia.integration.dto;

import java.util.List;

public record FilmSearchResponse(
        String keyword,
        int pagesCount,
        int searchFilmsCountResult,
        List<FilmDto> films
) {

    public record FilmDto(
            Long filmId,
            String nameRu,
            String nameEn,
            String type,
            String year,
            String description,
            String filmLength,
            List<CountryDto> countries,
            List<GenreDto> genres,
            String rating,
            int ratingVoteCount,
            String posterUrl,
            String posterUrlPreview
    ) {
    }

    public record CountryDto(
            String country
    ) {
    }

    public record GenreDto(
            String genre
    ) {
    }
}