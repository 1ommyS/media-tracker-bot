package com.indistudia.bot;

import com.indistudia.bot.command.*;
import com.indistudia.bot.statemachine.BotStateMachine;
import com.indistudia.service.FilmsProxy;
import com.indistudia.service.MediaService;
import com.indistudia.service.WatchEntryService;

import java.util.Arrays;
import java.util.Map;

public class CommandResolver {
    private final Map<String, Command> commands;

    /**
     * Конструктор роутера команд.
     *
     * <p>Точка расширения для FSM заложена здесь:
     * при добавлении полноценной реализации достаточно передать нужный {@link BotStateMachine},
     * не меняя публичный API команд.
     */
    public CommandResolver(
            FilmsProxy filmsProxy,
            MediaService mediaService,
            WatchEntryService watchEntryService,
            BotStateMachine botStateMachine
    ) {
        this.commands = Map.of(
                "/start", new StartCommand(),
                "/film", new FilmCommand(filmsProxy),
                "/progress", new ProgressCommand(mediaService, watchEntryService),
                "/history", new HistoryCommand(watchEntryService),
                "/flow", new FlowCommand(botStateMachine)
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
