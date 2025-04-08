package com.example.bot._for_shelter.command.question;

import com.example.bot._for_shelter.command.Command;
import com.example.bot._for_shelter.command.SendBotMessage;
import com.example.bot._for_shelter.mark_ups.MarkUps;
import com.example.bot._for_shelter.models.Condition;
import com.example.bot._for_shelter.models.Room;
import com.example.bot._for_shelter.repository.ConditionRepository;
import com.example.bot._for_shelter.repository.RoomRepository;
import com.example.bot._for_shelter.service.HelpService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

@Component
public class EndAddQuestionCommand implements Command {

    private final HelpService helpService;
    private final ConditionRepository conditionRepository;
    private final SendBotMessage sendBotMessage;
    private final MarkUps markUps;
    private final RoomRepository roomRepository;

    public EndAddQuestionCommand(HelpService helpService, RoomRepository roomRepository,
                                 ConditionRepository conditionRepository, SendBotMessage sendBotMessage,
                                 MarkUps markUps) {
        this.helpService = helpService;
        this.roomRepository = roomRepository;
        this.conditionRepository = conditionRepository;
        this.sendBotMessage = sendBotMessage;
        this.markUps = markUps;
    }

    @Override
    @Transactional
    public void execute(Update update) {
        String chatId = update.getCallbackQuery().getMessage().getChatId().toString();

        // Находим последнюю комнату без использования кеша
        Room roomWithStatusTrue = helpService.findLastRoomWithoutCashing(chatId);

        if (roomWithStatusTrue == null) {
            sendBotMessageWithText(update, "Не удалось найти комнату для завершения добавления вопросов.");
            return;
        }

        // Обновляем статус комнаты
        roomWithStatusTrue.setQuestionStatus(false);
        roomRepository.save(roomWithStatusTrue);

        // Обновляем состояние для чата
        Condition condition = conditionRepository.findByChatId(chatId).orElse(null);
        if (condition == null) {
            sendBotMessageWithText(update, "Не найдено состояние для текущего чата.");
            return;
        }

        condition.setCondition("Завершил добавление вопросов");
        conditionRepository.save(condition);

        // Формируем клавиатуру и сообщение
        long roomId = roomWithStatusTrue.getId();
        InlineKeyboardMarkup markUp = markUps.menuAfterAddQuestion(roomId);
        String answer = "Вы завершили добавление вопросов. Теперь выберите пункт:";

        SendMessage msg = sendBotMessage.createMessageWithKeyboardMarkUpWithTextUpdate(update, answer, markUp);
        sendBotMessage.sendMessage(msg);
    }

    @Override
    public boolean isSupport(String update) {
        return update != null && update.startsWith("end");
    }

    private void sendBotMessageWithText(Update update, String text) {
        SendMessage msg = sendBotMessage.createMessage(update, text);
        sendBotMessage.sendMessage(msg);
    }
}
