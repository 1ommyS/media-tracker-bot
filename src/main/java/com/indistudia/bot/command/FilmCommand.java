package com.indistudia.bot.command;

import com.indistudia.domain.Media;
import com.indistudia.service.FilmsProxy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class FilmCommand implements Command {
    private static final int FILM_LIMIT = 5;
    private final FilmsProxy filmsProxy;

    @Override
    public String execute(String... args) {
        String query = String.join(" ", args).trim();

        if (query.isBlank()) {
            return "Укажи название фильма: /film Интерстеллар";
        }

        List<Media> searchResponse = filmsProxy.findFilms(query);

        if (searchResponse.isEmpty()) {
            return "По запросу \"" + query + "\" ничего не найдено.";
        }

        StringBuilder messageBuilder = new StringBuilder("Результаты по запросу \"")
                .append(query)
                .append("\":\n\n");

        searchResponse
                .stream()
                .limit(FILM_LIMIT)
                .forEach(film -> messageBuilder.append(formatFilm(film)).append("\n\n"));

        return messageBuilder.toString().trim();
    }


    private String formatFilm(Media film) {
        String title = film.getTitle();
        String year = film.getYear().toString();

        return "- " + title + " (" + year + ")\n"
                + " MediaId: " + film.getExternalId() + "\n";
    }
}
