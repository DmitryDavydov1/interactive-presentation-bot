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
public class SetPasswordOnRoomCoomand implements Command {

    private final RoomRepository roomRepository;
    private final CreatorTheRoomRepository creatorTheRoomRepository;
    private final SendBotMessage sendBotMessage;
    private final HelpService helpService;

    public SetPasswordOnRoomCoomand(RoomRepository roomRepository, CreatorTheRoomRepository creatorTheRoomRepository, SendBotMessage sendBotMessage, HelpService helpService) {
        this.roomRepository = roomRepository;
        this.creatorTheRoomRepository = creatorTheRoomRepository;
        this.sendBotMessage = sendBotMessage;
        this.helpService = helpService;
    }

    @Override
    public void execute(Update update) {
        String chatId = String.valueOf(update.getMessage().getChatId());
        CreatorTheRoom creatorTheRoom = creatorTheRoomRepository.findByChatId(chatId);

        if (creatorTheRoom.getStatus().equals("создаю пароль")) {

            Room roomWithStatusTrue = helpService.findLastRoom(creatorTheRoom);

            roomWithStatusTrue.setPassword(update.getMessage().getText());
            creatorTheRoom.setStatus("пока не знаю зачем");
            roomWithStatusTrue.setQuestionStatus("Жду вопросов");


            roomRepository.save(roomWithStatusTrue);
            creatorTheRoomRepository.save(creatorTheRoom);


            SendMessage msg = new SendMessage();
            msg.setChatId(chatId);
            msg.setText("Пароль создан, теперь можете ввести первые вопросы ");

            sendBotMessage.sendMessage(msg);

        }
    }

    @Override
    public boolean isSupport(String update) {
        return false;
    }
}
