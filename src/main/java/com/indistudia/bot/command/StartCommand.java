package com.indistudia.bot.command;

import com.indistudia.bot.CommandContext;

/**
 * process message exact like /start
 */
public class StartCommand implements Command {
    @Override
    public String execute(CommandContext context, String... args) {
        return """
                Привет! Я помогу найти фильм по Кинопоиску.

                Доступные команды:
                /start - показать это сообщение
                /film <название> - найти фильм по названию
                /progress <mediaId> <status> - отметить прогресс (planned|watching|completed|dropped)
                /history <N> - показать последние N изменений по фильмам
                /flow start - запустить будущий сценарий state machine (черновик)
                """;
    }
}
