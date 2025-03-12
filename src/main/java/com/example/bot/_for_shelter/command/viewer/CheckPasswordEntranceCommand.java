package com.example.bot._for_shelter.command.viewer;

import com.example.bot._for_shelter.command.Command;
import com.example.bot._for_shelter.command.SendBotMessage;
import com.example.bot._for_shelter.models.Condition;
import com.example.bot._for_shelter.models.Room;
import com.example.bot._for_shelter.repository.ConditionRepository;
import com.example.bot._for_shelter.repository.RoomRepository;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

public class CheckPasswordEntranceCommand implements Command {

    private final RoomRepository roomRepository;
    private final ConditionRepository conditionRepository;
    private final SendBotMessage sendBotMessage;

    public CheckPasswordEntranceCommand(RoomRepository roomRepository, ConditionRepository conditionRepository, SendBotMessage sendBotMessage) {
        this.roomRepository = roomRepository;
        this.conditionRepository = conditionRepository;
        this.sendBotMessage = sendBotMessage;
    }

    @Override
    public void execute(Update update) {
        String msg = update.getMessage().getText();
        String chatId = update.getMessage().getChatId().toString();
        Condition condition = conditionRepository.findByChatId(chatId);

        String idRoom = condition.getCondition().split(" ")[2];
        Room room = roomRepository.findByIdForEntry(Integer.parseInt(idRoom)).orElse(null);

        SendMessage sendMessage;
        if (room == null) {

            if (room.getPassword().equals(msg)) {
                sendMessage = sendBotMessage.createMessage(update, "Пароль верный");
                sendBotMessage.sendMessage(sendMessage);
            } else {
                sendMessage = sendBotMessage.createMessage(update, "Пароль неверный");
                sendBotMessage.sendMessage(sendMessage);
            }
        }
    }

    @Override
    public boolean isSupport(String update) {
        if (update.startsWith("вводит пароль ")) {
            String numberPart = update.substring("вводит пароль ".length());
            return numberPart.matches("\\d+");
        }
        return false;
    }
}
