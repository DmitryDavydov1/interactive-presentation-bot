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
import java.util.Random;

@Component
public class CreateRoomCommand implements Command {

    private final CreatorTheRoomRepository creatorTheRoomRepository;
    private final SendBotMessage sendBotMessage;
    private final RoomRepository roomRepository;
    private final HelpService helpService;

    public CreateRoomCommand(CreatorTheRoomRepository creatorTheRoomRepository, SendBotMessage sendBotMessage, RoomRepository roomRepository, HelpService helpService) {
        this.creatorTheRoomRepository = creatorTheRoomRepository;
        this.sendBotMessage = sendBotMessage;

        this.roomRepository = roomRepository;
        this.helpService = helpService;
    }

    @Override
    public void execute(Update update) {


        String chatId = String.valueOf(update.getCallbackQuery().getMessage().getChatId());
        CreatorTheRoom creatorTheRoom = creatorTheRoomRepository.findByChatId(chatId);

        if (creatorTheRoom.getStatus().equals("пока не знаю зачем")) {

            Random random = new Random();
            int randomNumber = random.nextInt(1000) + 1;

            Room roomWithStatusTrue = helpService.findLastRoom(creatorTheRoom);

            if (roomWithStatusTrue != null) {
                roomWithStatusTrue.setStatus(false);
                roomRepository.save(roomWithStatusTrue);
            }

            Room room = new Room();
            room.setCreatorTheRoom(creatorTheRoom);
            room.setStatus(true);
            while (roomRepository.existsByIdForEntry(randomNumber)) {
                randomNumber = random.nextInt(1000) + 1;
            }
            room.setIdForEntry(randomNumber);
            creatorTheRoom.setStatus("создаю пароль");

            roomRepository.save(room);
            creatorTheRoomRepository.save(creatorTheRoom);

            String text = "Комната создана, ее ID для входа: " + randomNumber + "\n" +
                    "Теперь придумай пароль для комнаты.";

            SendMessage msg = sendBotMessage.createMessage(update, text);
            sendBotMessage.sendMessage(msg);


        }

    }

    @Override
    public boolean isSupport(String update) {
        return update.equals("create_room");


    }
}