package com.example.bot._for_shelter.command.room;

import com.example.bot._for_shelter.command.Command;
import com.example.bot._for_shelter.command.SendBotMessage;
import com.example.bot._for_shelter.models.CreatorTheRoom;
import com.example.bot._for_shelter.models.Room;
import com.example.bot._for_shelter.repository.CreatorTheRoomRepository;
import com.example.bot._for_shelter.repository.RoomRepository;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Component
public class SetPasswordOnRoomCoomand implements Command {

    private final RoomRepository roomRepository;
    private final CreatorTheRoomRepository creatorTheRoomRepository;
    private final SendBotMessage sendBotMessage;

    public SetPasswordOnRoomCoomand(RoomRepository roomRepository, CreatorTheRoomRepository creatorTheRoomRepository, SendBotMessage sendBotMessage) {
        this.roomRepository = roomRepository;
        this.creatorTheRoomRepository = creatorTheRoomRepository;
        this.sendBotMessage = sendBotMessage;
    }

    @Override
    public void execute(Update update) {
        String chatId = String.valueOf(update.getMessage().getChatId());
        CreatorTheRoom creatorTheRoom = creatorTheRoomRepository.findByChatId(chatId);

        if (creatorTheRoom.getStatus().equals("создаю пароль")) {

            List<Room> rooms = creatorTheRoom.getRoom();
            Room roomWithStatusTrue = rooms.stream()
                    .filter(Room::isStatus) // Фильтруем по статусу
                    .findFirst().get();

            roomWithStatusTrue.setPassword(update.getMessage().getText());
            creatorTheRoom.setStatus("пока не знаю зачем");
            roomWithStatusTrue.setQuestionStatus("Жду вопросов");


            roomRepository.save(roomWithStatusTrue);
            creatorTheRoomRepository.save(creatorTheRoom);


            SendMessage msg = new SendMessage();
            msg.setChatId(chatId);
            msg.setText("Пароль создан, теперь можете ввести первые вопросы");

            sendBotMessage.sendMessage(msg);

        }
    }

    @Override
    public boolean isSupport(String update) {
        return false;
    }
}
