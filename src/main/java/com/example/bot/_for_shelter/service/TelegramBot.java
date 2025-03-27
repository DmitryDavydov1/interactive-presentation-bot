package com.example.bot._for_shelter.service;

import com.example.bot._for_shelter.command.*;


import com.example.bot._for_shelter.config.BotConfig;
import com.example.bot._for_shelter.repository.ConditionRepository;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

import com.example.bot._for_shelter.models.Condition;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class TelegramBot extends TelegramLongPollingBot {

    final BotConfig config;
    /**
     * Список всех доступных команд бота
     */

    private final List<Command> commandList;
    private final ConditionRepository conditionRepository;

    public TelegramBot(BotConfig config, List<Command> commandList,
                       ConditionRepository conditionRepository,
                       StartCommand startCommand) {
        this.config = config;
        this.commandList = commandList;
        this.conditionRepository = conditionRepository;
        this.startCommand = startCommand;
    }

    private final StartCommand startCommand;


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

        if (update.hasCallbackQuery()) {
            commandList.stream()
                    .filter(command -> command.isSupport(update.getCallbackQuery().getData()))
                    .forEach(command -> {
                        command.execute(update);
                    });
        }
        if (update.hasMessage() && update.getMessage().hasText()) {


            if (update.getMessage().getText().equals("/start")) {
                startCommand.execute(update);
                return;
            }


            String chatId = String.valueOf(update.getMessage().getChatId());


            Condition condition = conditionRepository.findByChatId(chatId).orElse(null);
            commandList.stream()
                    .filter(command -> command.isSupport(condition.getCondition()))
                    .forEach(command -> {
                        command.execute(update);
                    });
        }
    }

    public int sendMessage(SendMessage sendMessage) throws TelegramApiException {
        Message sentMessage = execute(sendMessage);
        Integer messageId = sentMessage.getMessageId();

        System.out.println("Отправлено сообщение с message_id: " + messageId);
        return messageId;
    }
}




