package com.example.bot._for_shelter.service;

import com.example.bot._for_shelter.command.*;
import com.example.bot._for_shelter.command.question.CreateQuestionCommand;
import com.example.bot._for_shelter.command.question.EditQuestionCommand;
import com.example.bot._for_shelter.command.room.SetPasswordOnRoomCoomand;
import com.example.bot._for_shelter.config.BotConfig;
import com.example.bot._for_shelter.models.CreatorTheRoom;
import com.example.bot._for_shelter.repository.ConditionRepository;
import com.example.bot._for_shelter.repository.CreatorTheRoomRepository;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

import com.example.bot._for_shelter.models.Condition;

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


            Condition condition = conditionRepository.findByChatId(chatId);
            commandList.stream()
                    .filter(command -> command.isSupport(condition.getCondition()))
                    .forEach(command -> {
                        command.execute(update);
                    });
        }
    }
}




