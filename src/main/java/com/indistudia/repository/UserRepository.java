package com.indistudia.repository;

import com.indistudia.domain.User;
import org.hibernate.Session;

import java.util.Optional;

public class UserRepository {

    public Optional<User> findByTelegramId(Session session, Long telegramId) {
        return session.createQuery("from User u where u.telegramId = :telegramId", User.class)
                .setParameter("telegramId", telegramId)
                .uniqueResultOptional();
    }

    public Optional<User> findById(Session session, int id) {
        return Optional.ofNullable(session.find(User.class, id));
    }

    public void save(Session session, User user) {
        session.persist(user);
    }
}
