package com.indistudia.service;

import com.indistudia.cache.CacheProvider;
import com.indistudia.config.TransactionSessionManager;
import com.indistudia.domain.User;
import com.indistudia.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final TransactionSessionManager transactionSessionManager;

    private final Logger logger = LoggerFactory.getLogger(UserService.class);

    public Optional<User> findByTelegramId(Long telegramId) {
        var fromCache = CacheProvider.get("USER-" + telegramId);

        if (fromCache != null) {
            return (Optional<User>) fromCache;
        }

        var user = transactionSessionManager.inSession(session -> userRepository.findByTelegramId(session, telegramId));
        CacheProvider.set("USER-" + telegramId, user);
        return user;
    }

    public User findById(int userId) {
        return transactionSessionManager.inSession(session -> userRepository.findById(session, userId).orElseThrow(() -> {
            logger.atError().addKeyValue("userId", userId).log("User with this id not found");
            return new RuntimeException("User with id " + userId + " not found");
        }));
    }

    public void save(User user) {
        transactionSessionManager.inTx(session -> userRepository.save(session, user));
    }

    public User getOrCreateByTelegramId(String username, Long telegramId) {
        var userFromDb = findByTelegramId(telegramId);

        if (userFromDb.isPresent()) {
            return userFromDb.get();
        }
        var user = User.builder()
                .telegramId(telegramId)
                .username(username)
                .build();

        save(user);

        return user;
    }
}
