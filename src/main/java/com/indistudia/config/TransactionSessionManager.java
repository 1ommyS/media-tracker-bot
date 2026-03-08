package com.indistudia.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.function.Consumer;
import java.util.function.Function;

@RequiredArgsConstructor
@Slf4j
public class TransactionSessionManager {
    private final SessionFactory sessionFactory;

    public <T> T inSession(Function<Session, T> func) {
        try (var session = sessionFactory.openSession()) {
            return func.apply(session);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return null;
    }

    public void inTx(Consumer<Session> func) {
        try (var session = sessionFactory.openSession()) {
            session.inTransaction(tx -> func.accept(session));
        }
    }
}
