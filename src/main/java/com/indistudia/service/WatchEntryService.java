package com.indistudia.service;

import com.indistudia.config.TransactionSessionManager;
import com.indistudia.domain.Media;
import com.indistudia.domain.User;
import com.indistudia.domain.WatchEntry;
import com.indistudia.domain.vo.WatchEntryStatus;
import com.indistudia.repository.WatchEntryRepository;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
public class WatchEntryService {
    private final WatchEntryRepository watchEntryRepository;
    private final TransactionSessionManager transactionSessionManager;

    public void updateProgress(User user, Media media, WatchEntryStatus status) {
        transactionSessionManager.inTx(session -> {
            WatchEntry watchEntry = watchEntryRepository
                    .findByUserAndMedia(session, user.getId(), media.getId())
                    .orElseGet(() -> {
                        WatchEntry created = new WatchEntry();
                        created.setUser(user);
                        created.setMedia(media);
                        return created;
                    });

            watchEntry.setStatus(status);
            watchEntry.setStatusChangedAt(LocalDateTime.now());

            if (watchEntry.getId() == null) {
                watchEntryRepository.save(session, watchEntry);
            }
        });
    }

    public List<WatchEntry> findLatest(User user, int limit) {
        List<WatchEntry> entries = transactionSessionManager.inSession(session ->
                watchEntryRepository.findLatestByUserId(session, user.getId(), limit));
        return entries == null ? Collections.emptyList() : entries;
    }
}
