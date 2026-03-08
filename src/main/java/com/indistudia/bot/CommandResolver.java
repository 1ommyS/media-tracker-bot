package com.indistudia.bot;

import com.indistudia.bot.command.Command;
import com.indistudia.bot.command.ParseFilmCommand;
import com.indistudia.bot.command.StartCommand;
import com.indistudia.integration.KinopoiskHttpClient;

import java.util.HashMap;
import java.util.Map;

public class CommandResolver {

    public static void init(KinopoiskHttpClient kinopoiskHttpClient) {
        commands = Map.of(
                "/start", new StartCommand(),
                "/parse", new ParseFilmCommand(kinopoiskHttpClient)
        );
    }

    static Map<String, Command> commands = new HashMap<>();

    static Command resolve(String message) {
        String parsedMessage = message.trim();
        String[] parts = parsedMessage.split(" ");
        String commandName = parts[0];

        var commandHandler = commands.get(commandName);

        if (commandHandler == null) {
            throw new CommandNotFoundException();
        }

        return commandHandler;
    }
}
