package com.indistudia.bot;

import com.indistudia.config.AppConfig;
import com.indistudia.integration.KinopoiskHttpClient;
import com.indistudia.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

@Slf4j
public class MediaTrackerBot extends TelegramLongPollingBot {
    private final AppConfig appConfig;
    private final UserService userService;
    private final CommandResolver commandResolver;

    public MediaTrackerBot(AppConfig appConfig, UserService userService, KinopoiskHttpClient kinopoiskHttpClient) {
        super(appConfig.getTelegramConfig().telegramBotToken());
        this.appConfig = appConfig;
        this.userService = userService;
        this.commandResolver = new CommandResolver(kinopoiskHttpClient);
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText() || update.getMessage().getFrom() == null) {
            return;
        }
        handleMessage(update.getMessage());
    }

    @Override
    public String getBotUsername() {
        return appConfig.getTelegramConfig().telegramBotUsername();
    }

    private void handleMessage(Message message) {
        Long chatId = message.getChatId();
        String text = message.getText();
        User from = message.getFrom();

        userService.getOrCreateByTelegramId(from.getUserName(), from.getId());

        try {
            CommandResolver.ResolvedCommand resolvedCommand = commandResolver.resolve(text);
            String result = resolvedCommand.command().execute(resolvedCommand.args());
            sendMessage(chatId, result);
        } catch (CommandNotFoundException e) {
            sendMessage(chatId, e.getMessage());
        } catch (Exception e) {
            log.error("Failed to process message. chatId={}", chatId, e);
            sendMessage(chatId, "Произошла ошибка при обработке команды.");
        }
    }

    private void sendMessage(Long chatId, String text) {
        try {
            execute(buildSendMessage(chatId, text));
        } catch (Exception e) {
            log.error("Failed to send message. chatId={}", chatId, e);
        }
    }

    private SendMessage buildSendMessage(Long chatId, String text) {
        return SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .build();
    }
}
