package com.indistudia.bot.command;

/**
 * process message exact like /start
 */
public class StartCommand implements Command {
    @Override
    public String execute(String... args) {
        return """
                Привет! Я помогу найти фильм по Кинопоиску.

                Доступные команды:
                /start - показать это сообщение
                /film <название> - найти фильм по названию
                """;
    }
}
