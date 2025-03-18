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
    private final SendBotMessage sendBotMessage;
    private final RoomRepository roomRepository;

    public CheckIdForEntranceCommand(ConditionRepository conditionRepository, SendBotMessage sendBotMessage, RoomRepository roomRepository1) {
        this.conditionRepository = conditionRepository;

        this.sendBotMessage = sendBotMessage;
        this.roomRepository = roomRepository1;
    }

    @Override
    public void execute(Update update) {

        String chatId = update.getMessage().getChatId().toString();
        int message = Integer.parseInt(update.getMessage().getText());

        Condition condition = conditionRepository.findByChatId(chatId).orElse(null);
        Room roomWithStatusTrue = roomRepository.findByIdForEntry(message).orElse(null);

        sendMessage(update, condition, roomWithStatusTrue);

    }

    @Override
    public boolean isSupport(String update) {
        return update.equals("Ввожу id комнаты");
    }

    public void sendMessage(Update update, Condition condition, Room room) {
        SendMessage msg = sendBotMessage.createMessage(update, "Теперь введи пароль от комнаты");
        if (room == null) {
            msg.setText("Комната не найдена");
        } else {
            condition.setCondition("вводит пароль " + room.getId());
            conditionRepository.save(condition);
        }
        sendBotMessage.sendMessage(msg);
    }
}
