package com.indistudia.bot.command;

import com.indistudia.bot.CommandContext;
import com.indistudia.bot.statemachine.BotStateMachine;
import com.indistudia.bot.statemachine.StateMachineStartResult;
import lombok.RequiredArgsConstructor;

/**
 * Команда запуска диалогового сценария (FSM) для трекинга фильмов.
 *
 * <p>Почему отдельная команда:
 * это явная точка входа в многошаговый режим, чтобы не смешивать его с обычными одношаговыми командами.
 *
 * <p>Функциональные требования к будущей реализации сценария {@code film-tracking}:
 * <ul>
 *   <li>Шаг 1: запросить у пользователя название фильма.</li>
 *   <li>Шаг 2: показать найденные варианты и попросить выбрать mediaId.</li>
 *   <li>Шаг 3: запросить статус (planned/watching/completed/dropped).</li>
 *   <li>Шаг 4: подтвердить действие и записать прогресс.</li>
 *   <li>Шаг 5: отправить итоговый summary и завершить сценарий.</li>
 * </ul>
 *
 * <p>Важно:
 * команда регистрирует контракт использования FSM, но сами шаги еще не реализованы.
 */
@RequiredArgsConstructor
public class FlowCommand implements Command {
    private static final String FILM_TRACKING_FLOW = "film-tracking";
    private final BotStateMachine botStateMachine;

    @Override
    public String execute(CommandContext context, String... args) {
        if (args.length == 0 || !"start".equalsIgnoreCase(args[0])) {
            return "Формат: /flow start";
        }

        StateMachineStartResult result = botStateMachine.startFlow(context, FILM_TRACKING_FLOW);

        return switch (result) {
            case STARTED -> "Сценарий запущен. Отправь следующее сообщение для шага 1.";
            case ALREADY_RUNNING -> "Сценарий уже запущен. Продолжай текущий шаг.";
            case NOT_IMPLEMENTED -> "State machine подключена как контракт, но сценарий пока не реализован.";
        };
    }
}
