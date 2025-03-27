package com.example.bot._for_shelter.command;

import com.example.bot._for_shelter.service.TelegramBot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
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
        String chatId;
        if (update.hasMessage() && update.getMessage().hasText()) {
            chatId = String.valueOf(update.getMessage().getChatId());
        } else {
            chatId = String.valueOf(update.getCallbackQuery().getMessage().getChatId());
        }
        SendMessage msg = new SendMessage();
        msg.setChatId(chatId);
        msg.setText(message);
        return msg;
    }

    public void editMessage(SendMessage newMessage, int messageId) throws TelegramApiException {
        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(newMessage.getChatId());
        editMessageText.setText(newMessage.getText());
        editMessageText.setReplyMarkup(null);
        editMessageText.setMessageId(messageId);
        telegramBot.execute(editMessageText);

    }

    public SendMessage createMessageWithKeyboardMarkUpWithTextUpdate(Update update, String text, InlineKeyboardMarkup inlineKeyboardMarkup) {
        SendMessage msg = new SendMessage();
        String chatId;
        if (update.hasCallbackQuery()) {
            chatId = update.getCallbackQuery().getMessage().getChatId().toString();
        } else {
            chatId = String.valueOf(update.getMessage().getChatId());
        }
        msg.setChatId(chatId);
        msg.setText(text);
        msg.setReplyMarkup(inlineKeyboardMarkup);
        return msg;
    }

    public SendMessage createMessageWithKeyboardMarkUpWithCallbackUpdate(Update update, String text, InlineKeyboardMarkup inlineKeyboardMarkup) {
        SendMessage msg = new SendMessage();
        String chatId = String.valueOf(update.getCallbackQuery().getMessage().getChatId());
        msg.setChatId(chatId);
        msg.setText(text);
        msg.setReplyMarkup(inlineKeyboardMarkup);
        return msg;
    }

    public SendMessage sendMessageForAll(String chatId, String message) {
        SendMessage msg = new SendMessage();
        msg.setText(message);
        msg.setChatId(chatId);
        return msg;
    }

    public void deleteMessage(Update update) {
        String chatId = String.valueOf(update.getCallbackQuery().getFrom().getId());
        int messageId = update.getCallbackQuery().getMessage().getMessageId();

        DeleteMessage deleteMessage = new DeleteMessage();
        deleteMessage.setChatId(String.valueOf(chatId));
        deleteMessage.setMessageId(messageId);

        try {
            telegramBot.execute(deleteMessage); // Отправка запроса на удаление сообщения
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void deleteMessageWithMessageId(Update update, Integer messageId) {
        String chatId;
        if (update.hasCallbackQuery()) {
            chatId = String.valueOf(update.getCallbackQuery().getFrom().getId());
        } else {
            chatId = update.getMessage().getChatId().toString();
        }


        DeleteMessage deleteMessage = new DeleteMessage();
        deleteMessage.setChatId(String.valueOf(chatId));
        deleteMessage.setMessageId(messageId);

        try {
            telegramBot.execute(deleteMessage); // Отправка запроса на удаление сообщения
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

}
