package com.indistudia.domain;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "media")
public class Media {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "source_url", nullable = false)
    private String sourceUrl;

    @Column(name = "external_id", nullable = false, unique = true)
    private String externalId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private int year;

    @Column(nullable = false)
    private String posterUrl;

    private LocalDateTime updatedAt;
}
