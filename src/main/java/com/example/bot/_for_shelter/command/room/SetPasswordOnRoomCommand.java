package com.example.bot._for_shelter.command.room;

import com.example.bot._for_shelter.command.Command;
import com.example.bot._for_shelter.command.SendBotMessage;
import com.example.bot._for_shelter.models.Condition;
import com.example.bot._for_shelter.models.Room;
import com.example.bot._for_shelter.repository.ConditionRepository;
import com.example.bot._for_shelter.repository.RoomRepository;
import com.example.bot._for_shelter.service.HelpService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class SetPasswordOnRoomCommand implements Command {

    private final RoomRepository roomRepository;
    private final SendBotMessage sendBotMessage;
    private final HelpService helpService;
    private final ConditionRepository conditionRepository;
    private static final Logger logger = LoggerFactory.getLogger(SetPasswordOnRoomCommand.class);

    public SetPasswordOnRoomCommand(RoomRepository roomRepository,
                                    SendBotMessage sendBotMessage,
                                    HelpService helpService,
                                    ConditionRepository conditionRepository) {
        this.roomRepository = roomRepository;
        this.sendBotMessage = sendBotMessage;
        this.helpService = helpService;
        this.conditionRepository = conditionRepository;
    }

    @Override
    @Transactional
    public void execute(Update update) {
        String chatId = String.valueOf(update.getMessage().getChatId());
        int length = update.getMessage().getText().length();
        if (length > 20) {
            SendMessage sendMessage = sendBotMessage.createMessage(update, "Твой пароль слишком длинный");
            sendBotMessage.sendMessage(sendMessage);
            logger.warn("Слишком длинный пароль от юзера: {}, длинною: {}", chatId, length);
            return;
        }

        Room room = helpService.findLastRoom(chatId);

        room.setPassword(update.getMessage().getText());
        roomRepository.save(room);

        updateCondition(chatId);
        sendPasswordCreatedMessage(chatId);
    }

    @Override
    public boolean isSupport(String update) {
        return "создаю пароль".equals(update);
    }

    private void updateCondition(String chatId) {
        conditionRepository.findByChatId(chatId)
                .ifPresent(condition -> {
                    condition.setCondition("Добавляю запросы");
                    conditionRepository.save(condition);
                });
    }

    private void sendPasswordCreatedMessage(String chatId) {
        SendMessage msg = new SendMessage();
        msg.setChatId(chatId);
        msg.setText("Пароль создан, теперь можете ввести первые вопросы");
        sendBotMessage.sendMessage(msg);
    }
}
