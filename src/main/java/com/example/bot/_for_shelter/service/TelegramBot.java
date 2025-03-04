package com.example.bot._for_shelter.service;

import com.example.bot._for_shelter.command.*;
import com.example.bot._for_shelter.command.question.CreateQuestionCommand;
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

    public TelegramBot(BotConfig config, List<Command> commandList, CreatorTheRoomRepository creatorTheRoomRepository, SetPasswordOnRoomCoomand setPasswordOnRoomCoomand, CreateQuestionCommand createQuestionCommand, StartCommand startCommand) {
        this.config = config;
        this.commandList = commandList;
        this.creatorTheRoomRepository = creatorTheRoomRepository;
        this.setPasswordOnRoomCoomand = setPasswordOnRoomCoomand;
        this.createQuestionCommand = createQuestionCommand;
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
                if (creatorTheRoom.getStatus().equals("соз даю пароль")) {
                    setPasswordOnRoomCoomand.execute(update);
                } else {
                    List<Room> rooms = creatorTheRoom.getRoom();
                    Room roomWithStatusTrue = rooms.stream()
                            .filter(Room::isStatus) // Фильтруем по статусу
                            .findFirst().orElse(null);
                    if (roomWithStatusTrue != null) {
                        boolean need = roomWithStatusTrue.getQuestionStatus().equals("Жду вопросов");
                        if (need) {
                            createQuestionCommand.execute(update);
                        }
                    }
                }


            }
        }
    }
}



