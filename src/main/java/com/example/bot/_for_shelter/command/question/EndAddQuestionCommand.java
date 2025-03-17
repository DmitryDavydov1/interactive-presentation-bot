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
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

@Component
public class EndAddQuestionCommand implements Command {

    private final CreatorTheRoomRepository creatorTheRoomRepository;
    private final HelpService helpService;
    private final RoomRepository roomRepository;
    private final ConditionRepository conditionRepository;
    private final SendBotMessage sendBotMessage;
    private final MarkUps markUps;

    public EndAddQuestionCommand(CreatorTheRoomRepository creatorTheRoomRepository, HelpService helpService, RoomRepository roomRepository, ConditionRepository conditionRepository, SendBotMessage sendBotMessage, MarkUps markUps) {
        this.creatorTheRoomRepository = creatorTheRoomRepository;
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

        CreatorTheRoom creatorTheRoom = creatorTheRoomRepository.findByChatId(chatId);
        Condition condition = conditionRepository.findByChatId(chatId);
        Room roomWithStatusTrue = helpService.findLastRoom(creatorTheRoom);
        condition.setCondition("Завершил добавление вопросов");


        roomRepository.save(roomWithStatusTrue);
        conditionRepository.save(condition);
        int roomId = roomWithStatusTrue.getId();
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
