package com.indistudia.bot;

import com.indistudia.config.AppConfig;
import com.indistudia.bot.statemachine.BotStateMachine;
import com.indistudia.bot.statemachine.NoopBotStateMachine;
import com.indistudia.service.FilmsProxy;
import com.indistudia.service.MediaService;
import com.indistudia.service.UserService;
import com.indistudia.service.WatchEntryService;
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
    private final BotStateMachine botStateMachine;

    public MediaTrackerBot(
            AppConfig appConfig,
            UserService userService,
            FilmsProxy filmsProxy,
            MediaService mediaService,
            WatchEntryService watchEntryService,
            BotStateMachine botStateMachine
    ) {
        super(appConfig.getTelegramConfig().telegramBotToken());
        this.appConfig = appConfig;
        this.userService = userService;
        this.botStateMachine = botStateMachine;
        this.commandResolver = new CommandResolver(filmsProxy, mediaService, watchEntryService, botStateMachine);
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

        var appUser = userService.getOrCreateByTelegramId(from.getUserName(), from.getId());
        CommandContext commandContext = new CommandContext(appUser);

        // FR: если у пользователя активный сценарий, сначала даем FSM шанс обработать сообщение.
        // Реальная логика шагов пока отсутствует, но точка расширения уже зафиксирована.
        if (botStateMachine.hasActiveFlow(commandContext)) {
            var flowReply = botStateMachine.tryHandle(commandContext, text);
            if (flowReply.isPresent()) {
                sendMessage(chatId, flowReply.get());
                return;
            }
        }

        try {
            CommandResolver.ResolvedCommand resolvedCommand = commandResolver.resolve(text);
            String result = resolvedCommand.command().execute(commandContext, resolvedCommand.args());
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
