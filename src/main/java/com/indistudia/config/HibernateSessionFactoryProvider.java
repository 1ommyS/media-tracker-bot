package com.indistudia.config;

import com.indistudia.domain.*;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import java.util.HashMap;
import java.util.Map;

public class HibernateSessionFactoryProvider {
    private HibernateSessionFactoryProvider() {
    }

    public static SessionFactory build(AppConfig appConfig) {
        Map<String, Object> settings = new HashMap<>();

        settings.put("hibernate.connection.driver_class", "org.postgresql.Driver");
        settings.put("hibernate.connection.url", appConfig.getDbConfig().dbUrl());
        settings.put("hibernate.connection.username", appConfig.getDbConfig().dbUsername());
        settings.put("hibernate.connection.password", appConfig.getDbConfig().dbPassword());
        settings.put("hibernate.dialect", appConfig.getHibernateConfig().dialect());
        settings.put("hibernate.show_sql", Boolean.toString(appConfig.getHibernateConfig().showSql()));
        settings.put("hibernate.hbm2ddl.auto", appConfig.getHibernateConfig().hbm2ddlAuto());
        settings.put("hibernate.auto_commit", "true");

        StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .applySettings(settings)
                .build();

        try {
            return new MetadataSources(registry)
                    .addAnnotatedClass(User.class)
                    .addAnnotatedClass(Media.class)
                    .addAnnotatedClass(Person.class)
                    .addAnnotatedClass(WatchEntry.class)
                    .addAnnotatedClass(MediaPersonRole.class)
                    .addAnnotatedClass(Review.class)
                    .buildMetadata()
                    .buildSessionFactory();
        } catch (Exception e) {
            StandardServiceRegistryBuilder.destroy(registry);
            throw new RuntimeException(e);
        }

    }
}
