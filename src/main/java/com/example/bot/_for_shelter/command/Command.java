package com.example.bot._for_shelter.command;

import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Интерфейс для выполнения команд бота.
 */
public interface Command {

    /**
     * Выполняет команду, основываясь на переданном обновлении.
     *
     * @param update объект обновления Telegram, содержащий информацию о сообщении и чате.
     */
    void execute(Update update);

    /**
     * Проверяет, поддерживает ли команда указанный текст команды.
     *
     * @param command текст команды для проверки.
     * @return true, если команда поддерживается, иначе false.
     */
    boolean isSupport(Update update);
}