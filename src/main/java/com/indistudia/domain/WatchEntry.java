package com.indistudia.domain;

import com.indistudia.domain.vo.WatchEntryStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "watch_entries",
        uniqueConstraints = @UniqueConstraint(name = "uk_watch_entries_user_media", columnNames = {"user_id", "media_id"})
)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class WatchEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "media_id", nullable = false)
    private Media media;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private WatchEntryStatus status;

    @Column(name = "status_changed_at", nullable = false)
    private LocalDateTime statusChangedAt;
}