package com.indistudia.bot;

import com.indistudia.bot.command.Command;
import com.indistudia.bot.command.FilmCommand;
import com.indistudia.bot.command.StartCommand;
import com.indistudia.service.FilmsProxy;

import java.util.Arrays;
import java.util.Map;

public class CommandResolver {
    private final Map<String, Command> commands;

    public CommandResolver(FilmsProxy filmsProxy) {
        this.commands = Map.of(
                "/start", new StartCommand(),
                "/film", new FilmCommand(filmsProxy)
        );
    }

    public ResolvedCommand resolve(String message) {
        String parsedMessage = message.trim();
        String[] parts = parsedMessage.split("\\s+");
        String commandName = normalizeCommandName(parts[0]);

        var commandHandler = commands.get(commandName);

        if (commandHandler == null) {
            throw new CommandNotFoundException();
        }

        String[] args = Arrays.copyOfRange(parts, 1, parts.length);
        return new ResolvedCommand(commandHandler, args);
    }

    private String normalizeCommandName(String rawCommand) {
        int mentionIndex = rawCommand.indexOf('@');
        if (mentionIndex <= 0) {
            return rawCommand;
        }
        return rawCommand.substring(0, mentionIndex);
    }

    public record ResolvedCommand(Command command, String[] args) {
    }
}
