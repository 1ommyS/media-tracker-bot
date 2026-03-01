package com.indistudia.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Getter
@Setter
@ToString
public class AppConfig {
    private final TelegramConfig telegramConfig;

    private final KinopoiskConfig kinopoiskConfig;

    private final DbConfig dbConfig;

    private final HibernateConfig hibernateConfig;

    private AppConfig(TelegramConfig telegramConfig, KinopoiskConfig kinopoiskConfig, DbConfig dbConfig, HibernateConfig hibernateConfig) {
        this.telegramConfig = telegramConfig;
        this.kinopoiskConfig = kinopoiskConfig;
        this.dbConfig = dbConfig;
        this.hibernateConfig = hibernateConfig;
    }

    public static AppConfig load() {
        Properties properties = new Properties();

        try (InputStream inputStream = AppConfig.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (inputStream != null) {
                properties.load(inputStream);
            }
        } catch (IOException e) {
            throw new RuntimeException("Can't initialize project. ", e);
        }

        TelegramConfig tgConfig = new TelegramConfig(
                properties.getProperty("telegram.bot.token"),
                properties.getProperty("telegram.bot.username")
        );

        var kinopoiskConfig = new KinopoiskConfig(
                properties.getProperty("kinopoisk.base-url"),
                properties.getProperty("kinopoisk.api-key")
        );

        var dbConfig = new DbConfig(
                properties.getProperty("db.url"),
                properties.getProperty("db.username"),
                properties.getProperty("db.password")
        );

        var hibernateConfig = new HibernateConfig(
                properties.getProperty("hibernate.hbm2ddl.auto"),
                properties.getProperty("hibernate.dialect"),
                Boolean.parseBoolean(properties.getProperty("hibernate.show-sql"))
        );
        return new AppConfig(tgConfig, kinopoiskConfig, dbConfig, hibernateConfig);
    }
}
