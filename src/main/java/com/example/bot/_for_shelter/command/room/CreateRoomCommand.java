package com.example.bot._for_shelter.command.room;


import com.example.bot._for_shelter.command.Command;
import com.example.bot._for_shelter.command.SendBotMessage;
import com.example.bot._for_shelter.models.Condition;
import com.example.bot._for_shelter.models.CreatorTheRoom;
import com.example.bot._for_shelter.models.Room;
import com.example.bot._for_shelter.repository.ConditionRepository;
import com.example.bot._for_shelter.repository.CreatorTheRoomRepository;
import com.example.bot._for_shelter.repository.RoomRepository;
import com.example.bot._for_shelter.service.HelpService;
import jakarta.transaction.Transactional;
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
    private final ConditionRepository conditionRepository;

    public CreateRoomCommand(CreatorTheRoomRepository creatorTheRoomRepository, SendBotMessage sendBotMessage, RoomRepository roomRepository, HelpService helpService, ConditionRepository conditionRepository) {
        this.creatorTheRoomRepository = creatorTheRoomRepository;
        this.sendBotMessage = sendBotMessage;

        this.roomRepository = roomRepository;
        this.helpService = helpService;
        this.conditionRepository = conditionRepository;
    }

    @Override
    @Transactional
    public void execute(Update update) {

        String chatId = String.valueOf(update.getCallbackQuery().getMessage().getChatId());
        CreatorTheRoom creatorTheRoom = creatorTheRoomRepository.findByChatId(chatId);

        Room roomWithStatusTrue = helpService.findLastRoom(creatorTheRoom);
        if (roomWithStatusTrue != null) {
            roomWithStatusTrue.setStatus(false);
            roomRepository.save(roomWithStatusTrue);
        }
        Room room = new Room();
        room.setCreatorTheRoom(creatorTheRoom);
        room.setStatus(true);
        room.setQuestionStatus(true);
        room.setAnswerStatus(true);

        int random = makeRandomNumber();
        room.setIdForEntry(random);
        roomRepository.save(room);

        updateCondition(chatId);
        sendRoomCreatedMessage(update, random);
    }


    @Override
    public boolean isSupport(String update) {
        return update.equals("create_room");
    }


    private int makeRandomNumber() {
        Random random = new Random();
        int randomNumber = random.nextInt(1000) + 1;
        while (roomRepository.existsByIdForEntry(randomNumber)) {
            randomNumber = random.nextInt(1000) + 1;
        }
        return randomNumber;
    }


    private void updateCondition(String chatId) {
        Condition condition = conditionRepository.findByChatId(chatId).orElse(null);
        if (condition != null) {
            condition.setCondition("создаю пароль");
            conditionRepository.save(condition);
        } else {
            Condition condition1 = new Condition();
            condition1.setCondition("создаю пароль");
            condition1.setChatId(chatId);
            conditionRepository.save(condition1);
        }

    }


    private void sendRoomCreatedMessage(Update update, int roomId) {
        String text = "Комната создана, ее ID для входа: " + roomId + "\nТеперь придумай пароль для комнаты.";
        SendMessage msg = sendBotMessage.createMessage(update, text);
        sendBotMessage.sendMessage(msg);
    }

}