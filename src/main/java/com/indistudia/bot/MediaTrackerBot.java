package com.indistudia.bot;

import com.indistudia.bot.command.Command;
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

    public MediaTrackerBot(AppConfig appConfig, UserService userService, KinopoiskHttpClient kinopoiskHttpClient) {
        super(appConfig.getTelegramConfig().telegramBotToken());
        this.appConfig = appConfig;

        this.userService = userService;

        CommandResolver.init(kinopoiskHttpClient);
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText() || update.getMessage().getFrom() == null) {
            return;
        }
        Message message = update.getMessage();

        Long tgId = message.getChatId();
        String text = message.getText();

        final User from = message.getFrom();

        userService.getOrCreateByTelegramId(from.getUserName(), from.getId());

        Command handler = null;
        try {
            handler = CommandResolver.resolve(text);
        } catch (Exception e) {
            execute(tgId, e.getMessage());
            return;
        }

        var result = handler.execute(text);

        execute(tgId, result);
    }

    @Override
    public String getBotUsername() {
        return appConfig.getTelegramConfig().telegramBotUsername();
    }

    private void execute(Long chatId, String text) {
        try {
            execute(getSendMessage(chatId, text));
        } catch (Exception e) {
            log.atError().addKeyValue("ChatId", chatId).addKeyValue("Error", e.getMessage());
        }
    }

    private SendMessage getSendMessage(Long chatId, String text) {
        return SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .build();
    }
}
