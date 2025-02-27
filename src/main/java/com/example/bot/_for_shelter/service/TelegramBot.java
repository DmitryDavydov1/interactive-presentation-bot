package com.example.bot._for_shelter.service;

import com.example.bot._for_shelter.command.Command;
import com.example.bot._for_shelter.config.BotConfig;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

@Component
public class TelegramBot extends TelegramLongPollingBot {

    final BotConfig config;
    /**
     * Список всех доступных команд бота
     */

    private final List<Command> commandList;

    public TelegramBot(BotConfig config, List<Command> commandList) {
        this.config = config;
        this.commandList = commandList;
    }


    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    @Override
    public String getBotToken() {
        return config.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        commandList.stream()
                .filter(command -> command.isSupport(update))
                .forEach(command -> {
                    command.execute(update);
                });


    }
}




