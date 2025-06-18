package com.example.bot._for_shelter.command;

import com.example.bot._for_shelter.mark_ups.MarkUps;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

@Component
public class StartCommand implements Command {

    private final MarkUps markUps;
    private final SendBotMessage sendBotMessage;

    public StartCommand(MarkUps markUps, SendBotMessage sendBotMessage) {
        this.markUps = markUps;
        this.sendBotMessage = sendBotMessage;
    }

    @Override
    public void execute(Update update) {
        SendMessage message = new SendMessage();
        message.setChatId(update.getMessage().getChatId().toString());
        InlineKeyboardMarkup startButton = markUps.startMenuButton();
        message.setReplyMarkup(startButton);
        message.setText("Выберите роль");
        sendBotMessage.sendMessage(message);
    }

    @Override
    public boolean isSupport(String update) {
        return update.equals("/start");
    }
}
