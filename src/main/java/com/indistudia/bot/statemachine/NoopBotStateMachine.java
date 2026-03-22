package com.indistudia.bot.statemachine;

import com.indistudia.bot.CommandContext;

import java.util.Optional;

/**
 * Пустая реализация FSM.
 */
public class NoopBotStateMachine implements BotStateMachine {
    @Override
    public StateMachineStartResult startFlow(CommandContext context, String flowKey) {
        return StateMachineStartResult.NOT_IMPLEMENTED;
    }

    @Override
    public boolean hasActiveFlow(CommandContext context) {
        return false;
    }

    @Override
    public Optional<String> tryHandle(CommandContext context, String incomingText) {
        return Optional.empty();
    }
}
