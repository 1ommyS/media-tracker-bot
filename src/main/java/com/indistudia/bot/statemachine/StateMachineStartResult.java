package com.indistudia.bot.statemachine;

/**
 * Результат попытки запуска сценария через state machine.
 */
public enum StateMachineStartResult {
    /**
     * Сценарий запущен.
     */
    STARTED,
    /**
     * Сценарий уже запущен для пользователя, повторный старт не нужен.
     */
    ALREADY_RUNNING,
    /**
     * Сценарий не поддерживается или state machine пока не реализована.
     */
    NOT_IMPLEMENTED
}
