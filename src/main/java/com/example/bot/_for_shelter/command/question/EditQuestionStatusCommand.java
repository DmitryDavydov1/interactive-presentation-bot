package com.example.bot._for_shelter.command.question;

import com.example.bot._for_shelter.command.Command;
import com.example.bot._for_shelter.command.SendBotMessage;
import com.example.bot._for_shelter.models.Condition;
import com.example.bot._for_shelter.models.CreatorTheRoom;
import com.example.bot._for_shelter.models.Question;
import com.example.bot._for_shelter.models.Room;
import com.example.bot._for_shelter.repository.ConditionRepository;
import com.example.bot._for_shelter.repository.CreatorTheRoomRepository;
import com.example.bot._for_shelter.repository.QuestionRepository;
import com.example.bot._for_shelter.repository.RoomRepository;
import com.example.bot._for_shelter.service.HelpService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;


@Component
public class EditQuestionStatusCommand implements Command {
    private final CreatorTheRoomRepository creatorTheRoomRepository;
    private final RoomRepository roomRepository;
    private final SendBotMessage sendBotMessage;
    private final QuestionRepository questionRepository;
    private final HelpService helpService;
    private final ConditionRepository conditionRepository;

    public EditQuestionStatusCommand(CreatorTheRoomRepository creatorTheRoomRepository, RoomRepository roomRepository, SendBotMessage sendBotMessage, QuestionRepository questionRepository, HelpService helpService, ConditionRepository conditionRepository) {
        this.creatorTheRoomRepository = creatorTheRoomRepository;
        this.roomRepository = roomRepository;
        this.sendBotMessage = sendBotMessage;
        this.questionRepository = questionRepository;
        this.helpService = helpService;
        this.conditionRepository = conditionRepository;
    }

    @Override
    public void execute(Update update) {
        String updateMessage = update.getCallbackQuery().getData();
        String[] parts = updateMessage.split("-");
        String chatId = String.valueOf(update.getCallbackQuery().getFrom().getId());

        CreatorTheRoom creatorTheRoom = creatorTheRoomRepository.findByChatId(chatId);
        Room roomWithStatusTrue = helpService.findLastRoom(creatorTheRoom);

        assert roomWithStatusTrue != null;
        Condition condition = conditionRepository.findByChatId(chatId);
        if (condition != null) {
            condition.setCondition(parts[2]);
            conditionRepository.save(condition);
        }else {
            Condition condition1 = new Condition();
            condition1.setCondition(parts[2]);
            condition1.setChatId(chatId);

            conditionRepository.save(condition1);
        }

        roomWithStatusTrue.setEditQuestionStatus(parts[2]);
        roomRepository.save(roomWithStatusTrue);

        long questionId = Long.parseLong(parts[2]);
        Question question = questionRepository.findById(questionId).orElse(null);
        SendMessage sendMessage = sendBotMessage.createMessage(update, "Можешь ввести исправленный текст для вопроса: \n " +
                "«" + question.getText() + "»");

        sendBotMessage.sendMessage(sendMessage);
    }

    @Override
    public boolean isSupport(String update) {
        try {
            String[] parts = update.split("-");
            return parts[0].equals("edit");
        } catch (Exception e) {
            return false;
        }
    }
}
