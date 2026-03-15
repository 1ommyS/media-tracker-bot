package com.indistudia.repository;

import com.indistudia.domain.Media;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;

import java.util.List;

@RequiredArgsConstructor
public class MediaRepository {
    public void save(Session session, Media media) {
        session.persist(media);
    }

    public List<Media> findByQuery(Session session, String query) {
        String hql = """
                from Media m
                where lower(m.title) like lower(concat('%', :query, '%'))
                """;

        return session.createQuery(hql, Media.class)
                .setParameter("query", query)
                .getResultList();
    }
}
