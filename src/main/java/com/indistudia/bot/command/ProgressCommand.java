package com.indistudia.bot.command;

import com.indistudia.bot.CommandContext;
import com.indistudia.domain.Media;
import com.indistudia.domain.vo.WatchEntryStatus;
import com.indistudia.service.MediaService;
import com.indistudia.service.WatchEntryService;
import lombok.RequiredArgsConstructor;

import java.util.Locale;
import java.util.Optional;

@RequiredArgsConstructor
public class ProgressCommand implements Command {
    private final MediaService mediaService;
    private final WatchEntryService watchEntryService;

    @Override
    public String execute(CommandContext context, String... args) {
        if (args.length < 2) {
            return "Формат: /progress <mediaId> <status>. Статусы: planned, watching, completed, dropped";
        }

        String mediaId = args[0].trim();
        String rawStatus = args[1].trim();

        Optional<Media> media = mediaService.findByExternalId(mediaId);
        if (media.isEmpty()) {
            return "Фильм с mediaId " + mediaId + " не найден. Сначала найди его командой /film.";
        }

        Optional<WatchEntryStatus> status = parseStatus(rawStatus);
        if (status.isEmpty()) {
            return "Неизвестный статус: " + rawStatus + ". Доступно: planned, watching, completed, dropped";
        }

        watchEntryService.updateProgress(context.user(), media.get(), status.get());
        return "Обновлено: " + media.get().getTitle() + " -> " + normalizeStatus(status.get());
    }

    private Optional<WatchEntryStatus> parseStatus(String rawStatus) {
        String normalized = rawStatus.toLowerCase(Locale.ROOT);
        return switch (normalized) {
            case "planned" -> Optional.of(WatchEntryStatus.PLANNED);
            case "watching" -> Optional.of(WatchEntryStatus.WACTHING);
            case "completed" -> Optional.of(WatchEntryStatus.COMPLETED);
            case "dropped" -> Optional.of(WatchEntryStatus.DROPPED);
            default -> Optional.empty();
        };
    }

    private String normalizeStatus(WatchEntryStatus status) {
        return switch (status) {
            case PLANNED -> "planned";
            case WACTHING -> "watching";
            case COMPLETED -> "completed";
            case DROPPED -> "dropped";
        };
    }
}
