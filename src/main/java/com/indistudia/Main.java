package com.indistudia;

import com.indistudia.config.AppConfig;
import com.indistudia.config.HibernateSessionFactoryProvider;
import com.indistudia.domain.User;
import com.indistudia.repository.UserRepository;
import org.hibernate.SessionFactory;

import java.time.LocalDateTime;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        final AppConfig appConfig = AppConfig.load();
        final SessionFactory sessionFactory = HibernateSessionFactoryProvider.build(appConfig);
        Runtime.getRuntime().addShutdownHook(new Thread(sessionFactory::close));

        UserRepository userRepository = new UserRepository();

        var session = sessionFactory.openSession();

        userRepository.save(session, User.builder()
                .username("ewqewq")
                .telegramId(2L)
                .createdAt(LocalDateTime.now())
                .build());

        var session2 = sessionFactory.openSession();

        var user = userRepository.findById(session2, 1);
        System.out.println(user.orElseThrow(() -> new RuntimeException("User not found")));
    }
}