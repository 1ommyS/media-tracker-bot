package com.indistudia.bot.command;

import com.indistudia.bot.CommandContext;
import com.indistudia.domain.WatchEntry;
import com.indistudia.service.WatchEntryService;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class HistoryCommand implements Command {
    private static final int DEFAULT_LIMIT = 5;
    private static final int MAX_LIMIT = 50;
    private final WatchEntryService watchEntryService;

    @Override
    public String execute(CommandContext context, String... args) {
        int limit = parseLimit(args);
        if (limit <= 0) {
            return "Формат: /history <N>, где N от 1 до " + MAX_LIMIT;
        }

        List<WatchEntry> entries = watchEntryService.findLatest(context.user(), limit);
        if (entries.isEmpty()) {
            return "История пуста. Добавь прогресс через /progress.";
        }

        StringBuilder builder = new StringBuilder("Последние ")
                .append(entries.size())
                .append(" фильмов:\n\n");

        for (int i = 0; i < entries.size(); i++) {
            WatchEntry entry = entries.get(i);
            builder.append(i + 1)
                    .append(". ")
                    .append(entry.getMedia().getTitle())
                    .append(" (")
                    .append(entry.getMedia().getYear())
                    .append(")")
                    .append(" | ")
                    .append(formatStatus(entry))
                    .append(" | ")
                    .append(entry.getStatusChangedAt())
                    .append("\n");
        }

        return builder.toString().trim();
    }

    private int parseLimit(String... args) {
        if (args.length == 0) {
            return DEFAULT_LIMIT;
        }

        try {
            int raw = Integer.parseInt(args[0]);
            if (raw < 1 || raw > MAX_LIMIT) {
                return -1;
            }
            return raw;
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private String formatStatus(WatchEntry entry) {
        return switch (entry.getStatus()) {
            case PLANNED -> "planned";
            case WACTHING -> "watching";
            case COMPLETED -> "completed";
            case DROPPED -> "dropped";
        };
    }
}
