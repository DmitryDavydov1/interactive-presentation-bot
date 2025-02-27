package com.example.bot._for_shelter.command;


import com.example.bot._for_shelter.models.CreatorTheRoom;
import com.example.bot._for_shelter.models.Room;
import com.example.bot._for_shelter.repository.CreatorTheRoomRepository;
import com.example.bot._for_shelter.repository.RoomRepository;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;
import java.util.Optional;
import java.util.Random;

@Component
public class CreateRoomCommand implements Command {

    private final CreatorTheRoomRepository creatorTheRoomRepository;
    private final SendBotMessage sendBotMessage;
    private final RoomRepository roomRepository;

    public CreateRoomCommand(CreatorTheRoomRepository creatorTheRoomRepository, SendBotMessage sendBotMessage, RoomRepository roomRepository) {
        this.creatorTheRoomRepository = creatorTheRoomRepository;
        this.sendBotMessage = sendBotMessage;

        this.roomRepository = roomRepository;
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
            roomRepository.save(roomWithStatusTrue);
        }
        if (creatorTheRoom.getStatus().equals("пока не знаю зачем")) {
            Random random = new Random();
            int randomNumber = random.nextInt(1000) + 1;


            Room room = new Room();
            room.setCreatorTheRoom(creatorTheRoom);
            room.setStatus(true);
            while (roomRepository.existsByIdForEntry(randomNumber)) {
                randomNumber = random.nextInt(1000) + 1;
            }
            room.setIdForEntry(randomNumber);
            creatorTheRoom.setStatus("создаю пароль");

            String text = "Комната создана ее id для входа " + randomNumber;
            SendMessage msg = sendBotMessage.createMessage(update, text);
            sendBotMessage.sendMessage(msg);

            roomRepository.save(room);
            creatorTheRoomRepository.save(creatorTheRoom);
        }

    }

    @Override
    public boolean isSupport(Update update) {
        String chatId = String.valueOf(update.getMessage().getChatId());
        String text = update.getMessage().getText();
        CreatorTheRoom creatorTheRoom = creatorTheRoomRepository.findByChatId(chatId);
        if (text.equals("create_room")) {
            return true;
        }
        try {
            if (creatorTheRoom.getStatus().equals("создаю пароль")) {
                return true;
            }
        } catch (NullPointerException e) {
            return false;
        }
        return false;


    }
}