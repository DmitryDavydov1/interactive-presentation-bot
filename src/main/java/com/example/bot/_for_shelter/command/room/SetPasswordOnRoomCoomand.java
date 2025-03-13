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
    private final ConditionRepository conditionRepository;

    public SetPasswordOnRoomCoomand(RoomRepository roomRepository, CreatorTheRoomRepository creatorTheRoomRepository, SendBotMessage sendBotMessage, HelpService helpService, ConditionRepository conditionRepository) {
        this.roomRepository = roomRepository;
        this.creatorTheRoomRepository = creatorTheRoomRepository;
        this.sendBotMessage = sendBotMessage;
        this.helpService = helpService;
        this.conditionRepository = conditionRepository;
    }

    @Override
    public void execute(Update update) {

        String chatId = String.valueOf(update.getMessage().getChatId());
        CreatorTheRoom creatorTheRoom = creatorTheRoomRepository.findByChatId(chatId);

        Room roomWithStatusTrue = helpService.findLastRoom(creatorTheRoom);
        roomWithStatusTrue.setPassword(update.getMessage().getText());

        roomRepository.save(roomWithStatusTrue);
        creatorTheRoomRepository.save(creatorTheRoom);

        updateCondition(chatId);

        sendPasswordCreatedMessage(chatId);

    }


    @Override
    public boolean isSupport(String update) {
        return update.equals("создаю пароль");
    }


    private void sendPasswordCreatedMessage(String chatId) {
        SendMessage msg = new SendMessage();
        msg.setChatId(chatId);
        msg.setText("Пароль создан, теперь можете ввести первые вопросы ");
        sendBotMessage.sendMessage(msg);
    }


    private void updateCondition(String chatId) {
        Condition condition = conditionRepository.findByChatId(chatId);
        if (condition != null) {
            condition.setCondition("Добавляю запросы");
            conditionRepository.save(condition);
        } else {
            Condition condition1 = new Condition();
            condition1.setCondition("Добавляю запросы");
            condition1.setChatId(chatId);

            conditionRepository.save(condition1);
        }

    }
}

