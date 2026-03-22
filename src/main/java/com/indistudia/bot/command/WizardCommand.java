package com.indistudia.bot.command;

import com.indistudia.bot.CommandContext;
import com.indistudia.bot.statemachine.BotStateMachine;
import com.indistudia.bot.statemachine.StateMachineStartResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class WizardCommand implements Command{
    public static final String WIZARD_FLOW_KEY = "film-wizard";
    private final BotStateMachine stateMachine;

    @Override
    public String execute(CommandContext context, String... args) {
        if (args.length == 0)
            return "Формат: /wizzard старт";

        StateMachineStartResult result = stateMachine.startFlow(context, WIZARD_FLOW_KEY);

        return switch (result) {
            case STARTED -> """
                    Wizard запущен.
                    Шаг 1: отправьте название фильма
                    """.trim();
            case ALREADY_RUNNING -> "Продолжи шаг.";
            case NOT_IMPLEMENTED -> "У разработчика  бота пока лапки и он не дошел до реализации, пупу.";
        };
    }
}
