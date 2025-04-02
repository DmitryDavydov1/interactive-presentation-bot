package com.example.bot._for_shelter.command.viewer;

import com.example.bot._for_shelter.command.Command;
import com.example.bot._for_shelter.command.SendBotMessage;
import com.example.bot._for_shelter.models.Condition;
import com.example.bot._for_shelter.repository.ConditionRepository;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class EntranceRoomCommand implements Command {
    private final SendBotMessage sendBotMessage;
    private final ConditionRepository conditionRepository;

    public EntranceRoomCommand(SendBotMessage sendBotMessage, ConditionRepository conditionRepository) {
        this.sendBotMessage = sendBotMessage;
        this.conditionRepository = conditionRepository;
    }

    @Override
    public void execute(Update update) {
        String chatId = update.getCallbackQuery().getMessage().getChatId().toString();

        // Обновление или создание нового объекта Condition
        conditionRepository.findByChatId(chatId)
                .ifPresentOrElse(condition -> {
                    condition.setCondition("Ввожу id комнаты");
                    conditionRepository.save(condition);
                }, () -> {
                    Condition newCondition = new Condition();
                    newCondition.setChatId(chatId);
                    newCondition.setCondition("Ввожу id комнаты");
                    conditionRepository.save(newCondition);
                });

        // Отправка сообщения пользователю
        sendBotMessage.sendMessage(sendBotMessage.createMessage(update, "Введите ID комнаты"));
    }

    @Override
    public boolean isSupport(String update) {
        return "entrance-room".equalsIgnoreCase(update);
    }
}
