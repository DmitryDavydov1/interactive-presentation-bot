package com.example.bot._for_shelter.service;

import com.example.bot._for_shelter.command.*;
import com.example.bot._for_shelter.command.question.CreateQuestionCommand;
import com.example.bot._for_shelter.command.question.EditQuestionCommand;
import com.example.bot._for_shelter.command.question.EditQuestionStatusCommand;
import com.example.bot._for_shelter.command.room.SetPasswordOnRoomCoomand;
import com.example.bot._for_shelter.config.BotConfig;
import com.example.bot._for_shelter.models.CreatorTheRoom;
import com.example.bot._for_shelter.models.Room;
import com.example.bot._for_shelter.repository.CreatorTheRoomRepository;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Component
public class TelegramBot extends TelegramLongPollingBot {

    final BotConfig config;
    /**
     * Список всех доступных команд бота
     */

    private final List<Command> commandList;
    private final CreatorTheRoomRepository creatorTheRoomRepository;
    private final SetPasswordOnRoomCoomand setPasswordOnRoomCoomand;
    private final CreateQuestionCommand createQuestionCommand;
    private final EditQuestionCommand editQuestionCommand;
    private final HelpService helpService;

    public TelegramBot(BotConfig config, List<Command> commandList, CreatorTheRoomRepository creatorTheRoomRepository, SetPasswordOnRoomCoomand setPasswordOnRoomCoomand, CreateQuestionCommand createQuestionCommand, EditQuestionCommand editQuestionCommand, HelpService helpService, StartCommand startCommand) {
        this.config = config;
        this.commandList = commandList;
        this.creatorTheRoomRepository = creatorTheRoomRepository;
        this.setPasswordOnRoomCoomand = setPasswordOnRoomCoomand;
        this.createQuestionCommand = createQuestionCommand;
        this.editQuestionCommand = editQuestionCommand;
        this.helpService = helpService;
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
            CreatorTheRoom creatorTheRoom = creatorTheRoomRepository.findByChatId(chatId);


            if (creatorTheRoom != null) {
                if (creatorTheRoom.getStatus().equals("создаю пароль")) {
                    setPasswordOnRoomCoomand.execute(update);
                } else {
                    Room roomWithStatusTrue = helpService.findLastRoom(creatorTheRoom);
                    if (roomWithStatusTrue != null) {
                        boolean need = roomWithStatusTrue.getQuestionStatus().equals("Жду вопросов");
                        if (need) {
                            boolean room = !roomWithStatusTrue.getEditQuestionStatus().equals("не редактирую вопросы");
                            if (room) {
                                editQuestionCommand.execute(update);
                            } else {
                                createQuestionCommand.execute(update);
                            }
                        }
                    }
                }
            }
        }
    }
}



