package com.indistudia.bot.command;

import com.indistudia.bot.CommandContext;

public interface Command {
    String execute(CommandContext context, String... args);
}
