package com.indistudia.bot.command;

import com.indistudia.integration.KinopoiskHttpClient;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ParseFilmCommand implements Command {
    private final KinopoiskHttpClient httpClient;

    @Override
    public String execute(String rawMessage, String... args) {
        return "";
    }
}
