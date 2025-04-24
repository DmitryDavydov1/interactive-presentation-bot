package com.example.bot._for_shelter.command.viewer;

import com.example.bot._for_shelter.command.Command;
import com.example.bot._for_shelter.command.SendBotMessage;
import com.example.bot._for_shelter.command.room.SendStatisticCommand;
import com.example.bot._for_shelter.models.Condition;
import com.example.bot._for_shelter.models.Room;
import com.example.bot._for_shelter.repository.ConditionRepository;
import com.example.bot._for_shelter.repository.UserRepository;
import com.example.bot._for_shelter.service.HelpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Optional;

@Component
public class CheckIdForEntranceCommand implements Command {

    private final ConditionRepository conditionRepository;
    private final SendBotMessage sendBotMessage;
    private final HelpService helpService;
    private final UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(CheckIdForEntranceCommand.class);

    public CheckIdForEntranceCommand(ConditionRepository conditionRepository, SendBotMessage sendBotMessage, HelpService helpService, UserRepository userRepository) {
        this.conditionRepository = conditionRepository;
        this.sendBotMessage = sendBotMessage;
        this.helpService = helpService;
        this.userRepository = userRepository;
    }

    @Override
    public void execute(Update update) {
        String chatId = String.valueOf(update.getMessage().getChatId());
        long roomId = 0;
        try {
            roomId = Long.parseLong(update.getMessage().getText());
            if (roomId > 1000000) {
                logger.warn("Введен  id больше 1000000, Id комнаты: {}", roomId);
                SendMessage sendMessage = sendBotMessage.createMessage(update, "Неверный ID");
                sendBotMessage.sendMessage(sendMessage);
                return;
            }
        } catch (NumberFormatException e) {
            SendMessage msg = sendBotMessage.createMessage(update,
                    "iD должен быть из цифр");
            sendBotMessage.sendMessage(msg);
            logger.warn("Введен текстовый id для комнаты, Id комнаты: {}", roomId);
            return;
        }

        Room room = helpService.findRoomByIdForEntry(roomId);
        boolean alreadyInRoom = userRepository.existsUserInRoom(room.getId(), chatId);
        if (alreadyInRoom) {
            SendMessage msg = sendBotMessage.createMessage(update,
                    "Вы уже находитесь в этой комнате.");
            sendBotMessage.sendMessage(msg);
            return;
        }


        Optional<Condition> conditionOpt = conditionRepository.findByChatId(chatId);
        sendMessage(update, conditionOpt.orElse(null), room);
    }

    @Override
    public boolean isSupport(String update) {
        return "Ввожу id комнаты".equals(update);
    }

    private void sendMessage(Update update, Condition condition, Room room) {
        SendMessage msg = sendBotMessage.createMessage(update, room == null ? "Комната не найдена" : "Теперь введи пароль от комнаты");

        if (room != null) {
            condition.setCondition("вводит пароль " + room.getId());
            conditionRepository.save(condition);
        } else {
            logger.warn("Введен неправильный ID {}", update.getMessage().getChatId());
        }

        sendBotMessage.sendMessage(msg);
    }
}
