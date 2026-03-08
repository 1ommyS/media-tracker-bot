package com.indistudia.bot.command;

import com.indistudia.integration.KinopoiskHttpClient;
import com.indistudia.integration.dto.FilmSearchResponse;
import lombok.RequiredArgsConstructor;

import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class FilmCommand implements Command {
    private static final int FILM_LIMIT = 5;
    private final KinopoiskHttpClient httpClient;

    @Override
    public String execute(String... args) {
        String query = String.join(" ", args).trim();

        if (query.isBlank()) {
            return "Укажи название фильма: /film Интерстеллар";
        }

        Optional<FilmSearchResponse> searchResponse = httpClient.search(query, 1);

        if (searchResponse.isEmpty() || searchResponse.get().films() == null || searchResponse.get().films().isEmpty()) {
            return "По запросу \"" + query + "\" ничего не найдено.";
        }

        StringBuilder messageBuilder = new StringBuilder("Результаты по запросу \"")
                .append(query)
                .append("\":\n\n");

        searchResponse.get().films()
                .stream()
                .limit(FILM_LIMIT)
                .forEach(film -> messageBuilder.append(formatFilm(film)).append("\n\n"));

        return messageBuilder.toString().trim();
    }

    private String formatFilm(FilmSearchResponse.FilmDto film) {
        String title = firstNonBlank(film.nameRu(), film.nameEn(), "Без названия");
        String year = firstNonBlank(film.year(), "-");
        String rating = firstNonBlank(film.rating(), "-");
        String genres = film.genres() == null || film.genres().isEmpty()
                ? "не указаны"
                : film.genres().stream()
                .map(FilmSearchResponse.GenreDto::genre)
                .filter(genre -> genre != null && !genre.isBlank())
                .limit(3)
                .collect(Collectors.joining(", "));

        if (genres.isBlank()) {
            genres = "не указаны";
        }

        return "- " + title + " (" + year + ")\n"
                + "  Рейтинг: " + rating + "\n"
                + "  Жанры: " + genres;
    }

    private String firstNonBlank(String... values) {
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value;
            }
        }
        return "";
    }
}
