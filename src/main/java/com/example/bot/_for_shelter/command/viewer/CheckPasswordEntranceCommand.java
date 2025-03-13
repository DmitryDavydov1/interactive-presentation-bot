package com.example.bot._for_shelter.command.viewer;

import com.example.bot._for_shelter.command.Command;
import com.example.bot._for_shelter.command.SendBotMessage;
import com.example.bot._for_shelter.models.Condition;
import com.example.bot._for_shelter.models.Room;
import com.example.bot._for_shelter.repository.ConditionRepository;
import com.example.bot._for_shelter.repository.RoomRepository;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
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
        Room room = roomRepository.findById(Long.valueOf(idRoom)).orElse(null);

        SendMessage sendMessage;
        if (room == null) {
            System.out.println("s");
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
        try {
            String regex = "вводит пароль (\\d+)";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(update);

            return matcher.find();
        } catch (Exception e) {
            return false;
        }
    }
}
