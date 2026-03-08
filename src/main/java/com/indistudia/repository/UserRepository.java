package com.indistudia.repository;

import com.indistudia.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;

import java.util.Optional;

@Slf4j
public class UserRepository {

    public Optional<User> findByTelegramId(Session session, Long telegramId) {
        return session.createQuery("from User u where u.telegramId = :telegramId", User.class)
                .setParameter("telegramId", telegramId)
                .uniqueResultOptional();
    }

    public Optional<User> findById(Session session, int id) {
        return Optional.ofNullable(session.find(User.class, id));
    }

    public boolean save(Session session, User user) {
        try {
            session.persist(user);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
        return true;
    }
}
