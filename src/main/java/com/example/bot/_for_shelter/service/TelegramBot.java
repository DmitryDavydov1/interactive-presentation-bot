package com.example.bot._for_shelter.service;

import com.example.bot._for_shelter.command.Command;
import com.example.bot._for_shelter.config.BotConfig;
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
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();

            long chatId = update.getMessage().getChatId();


            switch (messageText) {
                case "/start":
                    startCommandReceived(chatId, update.getMessage().getChat().getFirstName());
                    break;
                default:
                    System.out.println("lsls");

            }
        }

    }


    private void startCommandReceived(long chatId, String name) {
        String answer = "Hi " + name + ", nice to meet you";
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(answer);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            System.out.println(e);
        }
    }


}
