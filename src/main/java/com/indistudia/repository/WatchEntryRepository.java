package com.indistudia.repository;

import com.indistudia.domain.WatchEntry;
import org.hibernate.Session;

import java.util.List;
import java.util.Optional;

public class WatchEntryRepository {
    public Optional<WatchEntry> findByUserAndMedia(Session session, Long userId, Long mediaId) {
        String hql = """
                from WatchEntry we
                where we.user.id = :userId and we.media.id = :mediaId
                """;

        return session.createQuery(hql, WatchEntry.class)
                .setParameter("userId", userId)
                .setParameter("mediaId", mediaId)
                .uniqueResultOptional();
    }

    public void save(Session session, WatchEntry watchEntry) {
        session.persist(watchEntry);
    }

    public List<WatchEntry> findLatestByUserId(Session session, Long userId, int limit) {
        String hql = """
                from WatchEntry we
                join fetch we.media
                where we.user.id = :userId
                order by we.statusChangedAt desc
                """;

        return session.createQuery(hql, WatchEntry.class)
                .setParameter("userId", userId)
                .setMaxResults(limit)
                .getResultList();
    }
}
