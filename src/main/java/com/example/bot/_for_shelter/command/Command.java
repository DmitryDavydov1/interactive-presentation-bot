package com.example.bot._for_shelter.command;

import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Интерфейс для выполнения команд бота.
 */

public interface Command {


    void execute(Update update);


    boolean isSupport(String update);
}