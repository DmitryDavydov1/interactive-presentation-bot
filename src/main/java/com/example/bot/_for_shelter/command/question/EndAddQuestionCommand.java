package com.example.bot._for_shelter.command.question;

import com.example.bot._for_shelter.command.Command;
import com.example.bot._for_shelter.command.SendBotMessage;
import com.example.bot._for_shelter.mark_ups.MarkUps;
import com.example.bot._for_shelter.models.Condition;
import com.example.bot._for_shelter.models.CreatorTheRoom;
import com.example.bot._for_shelter.models.Room;
import com.example.bot._for_shelter.repository.ConditionRepository;
import com.example.bot._for_shelter.repository.CreatorTheRoomRepository;
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

    public EndAddQuestionCommand(HelpService helpService, RoomRepository roomRepository, ConditionRepository conditionRepository, SendBotMessage sendBotMessage, MarkUps markUps, RoomRepository roomRepository1) {
        this.helpService = helpService;
        this.conditionRepository = conditionRepository;
        this.sendBotMessage = sendBotMessage;
        this.markUps = markUps;
        this.roomRepository = roomRepository1;
    }

    @Override
    @Transactional
    public void execute(Update update) {
        String chatId = update.getCallbackQuery().getMessage().getChatId().toString();

        Room roomWithStatusTrue = helpService.findLastRoomWithoutCashing(chatId);



        roomWithStatusTrue.setQuestionStatus(false);
        roomRepository.save(roomWithStatusTrue);
        Condition condition = conditionRepository.findByChatId(chatId).orElse(null);
        assert condition != null;
        condition.setCondition("Завершил добавление вопросов");
        conditionRepository.save(condition);


        long roomId = roomWithStatusTrue.getId();
        InlineKeyboardMarkup markUp = markUps.menuAfterAddQuestion(roomId);
        String answer = "Вы завершили добавление вопросов \nТеперь выберите пункт:";

        SendMessage msg = sendBotMessage.createMessageWithKeyboardMarkUpWithTextUpdate(update, answer, markUp);
        sendBotMessage.sendMessage(msg);
    }

    @Override
    public boolean isSupport(String update) {
        try {
            String[] parts = update.split("-");
            return parts[0].equals("end");
        } catch (Exception e) {
            return false;
        }
    }
}
