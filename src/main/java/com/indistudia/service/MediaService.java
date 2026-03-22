package com.indistudia.service;

import com.indistudia.config.TransactionSessionManager;
import com.indistudia.domain.Media;
import com.indistudia.repository.MediaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class MediaService {
    private final MediaRepository mediaRepository;
    private final TransactionSessionManager transactionSessionManager;

    public void save(Media media) {
        transactionSessionManager.inTx(session -> mediaRepository.save(session, media));
    }

    public void saveAll(List<Media> medias) {
        transactionSessionManager.inTx(session ->
                medias.forEach(media ->
                        mediaRepository.save(session, media)
                )
        );
    }

    public List<Media> findByQuery(String query) {
        return transactionSessionManager.inSession(session ->
                mediaRepository.findByQuery(session, query));
    }

    public Optional<Media> findByExternalId(String externalId) {
        return transactionSessionManager.inSession(session ->
                mediaRepository.findByExternalId(session, externalId));
    }
}
