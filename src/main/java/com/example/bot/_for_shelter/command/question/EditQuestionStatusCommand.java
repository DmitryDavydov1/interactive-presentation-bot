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

    private final SendBotMessage sendBotMessage;
    private final QuestionRepository questionRepository;
    private final ConditionRepository conditionRepository;

    public EditQuestionStatusCommand(SendBotMessage sendBotMessage, QuestionRepository questionRepository, ConditionRepository conditionRepository) {
        this.sendBotMessage = sendBotMessage;
        this.questionRepository = questionRepository;
        this.conditionRepository = conditionRepository;
    }


    @Override
    public void execute(Update update) {
        String updateMessage = update.getCallbackQuery().getData();
        String[] parts = updateMessage.split("-");
        String chatId = String.valueOf(update.getCallbackQuery().getFrom().getId());

        Condition condition = conditionRepository.findByChatId(chatId);
        condition.setCondition(parts[2]);
        conditionRepository.save(condition);

        sendMessage(update, Long.parseLong(parts[2]));
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


    private void sendMessage(Update update, long questionId) {
        Question question = questionRepository.findById(questionId).orElse(null);
        SendMessage sendMessage = sendBotMessage.createMessage(update, "Можешь ввести исправленный текст для вопроса: \n " +
                "«" + question.getText() + "»");

        sendBotMessage.sendMessage(sendMessage);
    }
}
