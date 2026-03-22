package com.indistudia.bot.statemachine.flow.wizard;

import com.indistudia.bot.CommandContext;
import com.indistudia.bot.command.WizardCommand;
import com.indistudia.bot.statemachine.BotStateMachine;
import com.indistudia.bot.statemachine.StateMachineStartResult;
import com.indistudia.domain.Media;
import com.indistudia.domain.vo.WatchEntryStatus;
import com.indistudia.service.FilmsProxy;
import com.indistudia.service.MediaService;
import com.indistudia.service.WatchEntryService;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
public class WizardFlow implements BotStateMachine {
    private final FilmsProxy filmsProxy;
    private final WatchEntryService watchEntryService;

    /*
    map ключ которой -- ID пользователя
    значение - инфа по нему в рамках этого флоу.
     */
    private final Map<Long, WizardSession> sessions = new ConcurrentHashMap<>();

    @Override
    public StateMachineStartResult startFlow(CommandContext context, String flowKey) {
        if (!WizardCommand.WIZARD_FLOW_KEY.equals(flowKey)) {
            return StateMachineStartResult.NOT_IMPLEMENTED;
        }

        Long userId = context.user().getId();

        if (sessions.containsKey(userId)) {
            return StateMachineStartResult.ALREADY_RUNNING;
        }

        sessions.put(userId, new WizardSession());

        return StateMachineStartResult.STARTED;
    }

    @Override
    public boolean hasActiveFlow(CommandContext context) {
        return sessions.containsKey(context.user().getId());
    }

    @Override
    public Optional<String> tryHandle(CommandContext context, String incomingText) {
        WizardSession session = sessions.get(context.user().getId());

        if (session == null) {
            return Optional.empty();
        }

        String text = incomingText.trim();

        if ("/cancel".equalsIgnoreCase(text)) {
            sessions.remove(context.user().getId());
            return Optional.of("Команда отменена");
        }

        return switch (session.getStep()) {
            case WAITING_TITLE -> Optional.of(handleTitle(session, text));
            case WAITING_MEDIA_ID -> Optional.of(handleMediaId(session, text));
            case WAITING_PROGRESS_STATUS -> Optional.of(handleStatus(context, session, text));
        };
    }

    private String handleTitle(WizardSession session, String text) {
        List<Media> films = filmsProxy.findFilms(text);

        if (films.isEmpty()) {
            return "Ничего не найдено! Повтори попытку";
        }

        session.setSearchQuery(text);
        session.setCandidates(films);
        session.setStep(WizardStep.WAITING_MEDIA_ID);

        var sb = new StringBuilder();

        for (Media media : films) {
            sb.append(media.formatFilm()).append("\n");
        }

        sb.append("Выбери фильм и отправь его mediaId");
        return sb.toString();
    }

    private String handleMediaId(WizardSession session, String mediaId) {
        if (mediaId.isBlank()) return "Отправь корректный mediaId";

        Optional<Media> selected = session.getCandidates().stream().filter(media -> media.getExternalId().equals(mediaId)).findFirst();

        if (selected.isEmpty()) {
            return "Отправь корректный mediaId";
        }

        session.setSelectedMedia(selected.get());
        session.setStep(WizardStep.WAITING_PROGRESS_STATUS);

        return """
                Отправь статус фильма, который ты хочешь поставить:  
                COMPLETED,PLANNED,WACTHING, DROPPED
                """.trim();
    }


    private String handleStatus(CommandContext context, WizardSession session, String rawStatus) {
        Optional<WatchEntryStatus> status = parseStatus(rawStatus);

        if (status.isEmpty()) {
            return "Некорректный статус.  Отправьте один из  COMPLETED,PLANNED,WACTHING, DROPPED";
        }

        watchEntryService.updateProgress(context.user(), session.getSelectedMedia(), status.get());

        Media media = session.getSelectedMedia();
        sessions.remove(context.user().getId());

        return "Готово! Для медиа " + media.getTitle() + " завершен процесс";
    }

    private Optional<WatchEntryStatus> parseStatus(String rawStatus) {
        return switch (rawStatus.trim()) {
            case "COMPLETED" -> Optional.of(WatchEntryStatus.COMPLETED);
            case "PLANNED" -> Optional.of(WatchEntryStatus.PLANNED);
            case "WACTHING" -> Optional.of(WatchEntryStatus.WACTHING);
            case "DROPPED" -> Optional.of(WatchEntryStatus.DROPPED);
            default -> Optional.empty();
        };
    }
}
