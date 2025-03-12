package com.example.bot._for_shelter.command.viewer;

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

@Component
public class CheckIdForEntranceCommand implements Command {
    private final ConditionRepository conditionRepository;
    private final CreatorTheRoomRepository creatorTheRoomRepository;
    private final SendBotMessage sendBotMessage;
    private final RoomRepository roomRepository;

    public CheckIdForEntranceCommand(RoomRepository roomRepository, ConditionRepository conditionRepository, CreatorTheRoomRepository creatorTheRoomRepository, HelpService helpService, SendBotMessage sendBotMessage, RoomRepository roomRepository1) {
        this.conditionRepository = conditionRepository;

        this.creatorTheRoomRepository = creatorTheRoomRepository;
        this.sendBotMessage = sendBotMessage;
        this.roomRepository = roomRepository1;
    }

    @Override
    public void execute(Update update) {

        String chatId = update.getMessage().getChatId().toString();
        int message = Integer.parseInt(update.getMessage().getText());


        Condition condition = conditionRepository.findByChatId(chatId);


        Room roomWithStatusTrue = roomRepository.findByIdForEntry(message).orElse(null);
        SendMessage msg = sendBotMessage.createMessage(update, "Теперь введи пароль от комнаты");
        if (roomWithStatusTrue == null) {
            msg.setText("Комната не найдена");
        } else {
            condition.setCondition("вводит пароль " + roomWithStatusTrue);
            conditionRepository.save(condition);
        }
        sendBotMessage.sendMessage(msg);

    }

    @Override
    public boolean isSupport(String update) {
        return update.equals("Ввожу id комнаты");
    }
}
