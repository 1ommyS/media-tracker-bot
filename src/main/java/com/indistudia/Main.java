package com.indistudia;

import com.indistudia.bot.MediaTrackerBot;
import com.indistudia.config.AppConfig;
import com.indistudia.config.HibernateSessionFactoryProvider;
import com.indistudia.config.ObjectMapperConfiguration;
import com.indistudia.config.TransactionSessionManager;
import com.indistudia.integration.KinopoiskHttpClient;
import com.indistudia.repository.UserRepository;
import com.indistudia.service.UserService;
import org.hibernate.SessionFactory;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        final AppConfig appConfig = AppConfig.load();
        final SessionFactory sessionFactory = HibernateSessionFactoryProvider.build(appConfig);
        Runtime.getRuntime().addShutdownHook(new Thread(sessionFactory::close));
        final var txSessionManager = new TransactionSessionManager(sessionFactory);
        final var userRepo = new UserRepository();
        var jackson = ObjectMapperConfiguration.initJackson();

        final UserService userService = new UserService(userRepo, txSessionManager);

        KinopoiskHttpClient kinopoiskHttpClient = new KinopoiskHttpClient(appConfig, jackson);
        MediaTrackerBot mediaTrackerBot = new MediaTrackerBot(appConfig, userService, kinopoiskHttpClient);


        var response = kinopoiskHttpClient.search("Люди в черном", 1).get();

        response.films().forEach(System.out::println);

//        try {
//            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
//            botsApi.registerBot(mediaTrackerBot);
//            new CountDownLatch(1).await();
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
    }
}