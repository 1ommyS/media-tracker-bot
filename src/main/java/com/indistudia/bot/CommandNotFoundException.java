package com.indistudia.bot;

public class CommandNotFoundException extends RuntimeException {
    public CommandNotFoundException() {
        super("Неизвестная команда. Используй /start для списка команд.");
    }
}
