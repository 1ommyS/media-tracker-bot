package com.indistudia.bot.command;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public interface Command {
    String execute(String rawMessage, String... args);
}
