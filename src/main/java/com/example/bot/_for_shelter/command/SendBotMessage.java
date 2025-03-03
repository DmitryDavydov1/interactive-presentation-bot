package com.example.bot._for_shelter.command;

import com.example.bot._for_shelter.service.TelegramBot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Service
public class SendBotMessage {


    private final TelegramBot telegramBot;

    @Autowired
    @Lazy
    public SendBotMessage(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }

    public void sendMessage(SendMessage message) {
        try {
            telegramBot.execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public SendMessage createMessage(Update update, String message) {
        String chatId = String.valueOf(update.getCallbackQuery().getMessage().getChatId());
        SendMessage msg = new SendMessage();
        msg.setChatId(chatId);
        msg.setText(message);
        return msg;
    }
}
