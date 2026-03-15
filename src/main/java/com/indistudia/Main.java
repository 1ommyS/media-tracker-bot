package com.indistudia;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.indistudia.bot.MediaTrackerBot;
import com.indistudia.config.AppConfig;
import com.indistudia.config.HibernateSessionFactoryProvider;
import com.indistudia.config.ObjectMapperConfiguration;
import com.indistudia.config.TransactionSessionManager;
import com.indistudia.integration.KinopoiskHttpClient;
import com.indistudia.repository.MediaRepository;
import com.indistudia.repository.UserRepository;
import com.indistudia.service.FilmsProxy;
import com.indistudia.service.MediaService;
import com.indistudia.service.UserService;
import org.hibernate.SessionFactory;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.concurrent.CountDownLatch;

public class Main {
    public static void main(String[] args) {
        AppConfig appConfig = AppConfig.load();
        SessionFactory sessionFactory = createSessionFactory(appConfig);
        registerShutdownHook(sessionFactory);

        UserService userService = createUserService(sessionFactory);
        MediaService mediaService = createMediaService(sessionFactory);
        KinopoiskHttpClient kinopoiskHttpClient = createKinopoiskHttpClient(appConfig);
        FilmsProxy filmsProxy = new FilmsProxy(mediaService, kinopoiskHttpClient);
        MediaTrackerBot mediaTrackerBot = createBot(appConfig, userService, filmsProxy);

        startBot(mediaTrackerBot);
    }

    private static SessionFactory createSessionFactory(AppConfig appConfig) {
        return HibernateSessionFactoryProvider.build(appConfig);
    }

    private static void registerShutdownHook(SessionFactory sessionFactory) {
        Runtime.getRuntime().addShutdownHook(new Thread(sessionFactory::close));
    }

    private static UserService createUserService(SessionFactory sessionFactory) {
        TransactionSessionManager txSessionManager = new TransactionSessionManager(sessionFactory);
        UserRepository userRepo = new UserRepository();
        return new UserService(userRepo, txSessionManager);
    }

    private static MediaService createMediaService(SessionFactory sessionFactory) {
        TransactionSessionManager txSessionManager = new TransactionSessionManager(sessionFactory);
        var mediaRepo = new MediaRepository();
        return new MediaService(mediaRepo, txSessionManager);
    }

    private static KinopoiskHttpClient createKinopoiskHttpClient(AppConfig appConfig) {
        ObjectMapper jackson = ObjectMapperConfiguration.initJackson();
        return new KinopoiskHttpClient(appConfig, jackson);
    }

    private static MediaTrackerBot createBot(
            AppConfig appConfig,
            UserService userService,
            FilmsProxy filmsProxy
    ) {
        return new MediaTrackerBot(appConfig, userService, filmsProxy);
    }


    private static void startBot(MediaTrackerBot mediaTrackerBot) {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(mediaTrackerBot);
            new CountDownLatch(1).await();
        } catch (Exception e) {
            throw new RuntimeException("Failed to start telegram bot", e);
        }
    }
}
