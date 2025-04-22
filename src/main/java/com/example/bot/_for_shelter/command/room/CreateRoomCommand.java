package com.example.bot._for_shelter.command.room;

import com.example.bot._for_shelter.command.Command;
import com.example.bot._for_shelter.command.SendBotMessage;
import com.example.bot._for_shelter.models.Condition;
import com.example.bot._for_shelter.models.Room;
import com.example.bot._for_shelter.models.User;
import com.example.bot._for_shelter.repository.ConditionRepository;
import com.example.bot._for_shelter.repository.RoomRepository;
import com.example.bot._for_shelter.repository.UserRepository;
import com.example.bot._for_shelter.service.HelpService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class CreateRoomCommand implements Command {


    private final SendBotMessage sendBotMessage;
    private final RoomRepository roomRepository;
    private final HelpService helpService;
    private final ConditionRepository conditionRepository;
    private final UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(CreateRoomCommand.class);

    public CreateRoomCommand(SendBotMessage sendBotMessage, RoomRepository roomRepository, HelpService helpService, ConditionRepository conditionRepository, UserRepository userRepository) {
        this.sendBotMessage = sendBotMessage;
        this.roomRepository = roomRepository;
        this.helpService = helpService;
        this.conditionRepository = conditionRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public void execute(Update update) {


        String chatId = getChatId(update);


        User user = userRepository.findByChatId(chatId).orElse(null);


        // Проверка существующей комнаты
        Room roomWithStatusTrue = helpService.findLastRoom(chatId);
        if (roomWithStatusTrue != null) {
            roomWithStatusTrue.setStatus(false);
            roomRepository.save(roomWithStatusTrue);
        } else {
            Room updateRoom = helpService.updateRoom(user);

            updateCondition(chatId);
            sendRoomCreatedMessage(update, updateRoom.getIdForEntry());
            return;
        }

        // Обновление или создание новой комнаты
        if (user != null) {
            Room updateRoom = helpService.updateRoom(user);
            logger.info("Создана новая комната, ждем пароль");
            updateCondition(chatId);
            sendRoomCreatedMessage(update, updateRoom.getIdForEntry());
        }

    }

    @Override
    public boolean isSupport(String update) {
        return "create_room".equals(update);
    }

    private String getChatId(Update update) {
        // Дополнительная проверка, если в update нет нужной информации
        if (update.getCallbackQuery() != null && update.getCallbackQuery().getMessage() != null) {
            return String.valueOf(update.getCallbackQuery().getMessage().getChatId());
        }
        return "";  // Или можно выбросить исключение
    }

    private void updateCondition(String chatId) {
        Condition condition = conditionRepository.findByChatId(chatId).orElseGet(() -> createCondition(chatId));
        condition.setCondition("создаю пароль");
        conditionRepository.save(condition);
    }

    private Condition createCondition(String chatId) {
        Condition condition = new Condition();
        condition.setChatId(chatId);
        condition.setCondition("создаю пароль");
        return condition;
    }

    private void sendRoomCreatedMessage(Update update, int roomId) {
        String text = "Комната создана, ее ID для входа: " + roomId + "\nТеперь придумай пароль для комнаты.";
        SendMessage msg = sendBotMessage.createMessage(update, text);
        sendBotMessage.sendMessage(msg);
    }


}
