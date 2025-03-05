package com.example.bot._for_shelter.command.room;

import com.example.bot._for_shelter.command.Command;
import com.example.bot._for_shelter.command.SendBotMessage;
import com.example.bot._for_shelter.models.CreatorTheRoom;
import com.example.bot._for_shelter.models.Room;
import com.example.bot._for_shelter.repository.CreatorTheRoomRepository;
import com.example.bot._for_shelter.repository.RoomRepository;
import com.example.bot._for_shelter.service.HelpService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Component
public class ChangeStatusQuestionInCommand implements Command {
    private final CreatorTheRoomRepository creatorTheRoomRepository;
    private final RoomRepository roomRepository;
    private final SendBotMessage sendBotMessage;
    private final HelpService helpService;

    public ChangeStatusQuestionInCommand(CreatorTheRoomRepository creatorTheRoomRepository, RoomRepository roomRepository, SendBotMessage sendBotMessage, HelpService helpService) {
        this.creatorTheRoomRepository = creatorTheRoomRepository;
        this.roomRepository = roomRepository;
        this.sendBotMessage = sendBotMessage;
        this.helpService = helpService;
    }

    @Override
    public void execute(Update update) {

        String chatId = String.valueOf(update.getMessage().getChatId());
        CreatorTheRoom creatorTheRoom = creatorTheRoomRepository.findByChatId(chatId);
        Room roomWithStatusTrue = helpService.findLastRoom(creatorTheRoom);

        assert roomWithStatusTrue != null;
        roomWithStatusTrue.setQuestionStatus("Жду вопросов");
        roomRepository.save(roomWithStatusTrue);

        SendMessage msg = sendBotMessage.createMessage(update, "Вводите свои вопросы");
        sendBotMessage.sendMessage(msg);
    }

    @Override
    public boolean isSupport(String update) {
        return update.equals("change-status");
    }
}
