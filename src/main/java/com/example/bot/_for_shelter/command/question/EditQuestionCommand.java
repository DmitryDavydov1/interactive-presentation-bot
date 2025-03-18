package com.example.bot._for_shelter.command.question;

import com.example.bot._for_shelter.command.Command;
import com.example.bot._for_shelter.command.SendBotMessage;
import com.example.bot._for_shelter.mark_ups.MarkUps;
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
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.List;

@Component
public class EditQuestionCommand implements Command {

    private final QuestionRepository questionRepository;

    private final MarkUps markUps;
    private final SendBotMessage sendBotMessage;

    private final ConditionRepository conditionRepository;

    public EditQuestionCommand(QuestionRepository questionRepository, MarkUps markUps, SendBotMessage sendBotMessage,
                               ConditionRepository conditionRepository) {
        this.questionRepository = questionRepository;
        this.markUps = markUps;
        this.sendBotMessage = sendBotMessage;
        this.conditionRepository = conditionRepository;
    }

    @Override
    public void execute(Update update) {
        String textMessage = update.getMessage().getText();
        String chatId = String.valueOf(update.getMessage().getChatId());

        Condition condition = conditionRepository.findByChatId(chatId).orElse(null);
        Question question = questionRepository.findById(Long.valueOf(condition.getCondition())).orElse(null);
        InlineKeyboardMarkup markUp = markUps.questionActivitiesButton(Long.valueOf(condition.getCondition()));
        question.setText(textMessage);
        condition.setCondition("Добавляю запросы");

        conditionRepository.save(condition);
        questionRepository.save(question);



        sendMessage(update, question, markUp);
    }

    @Override
    public boolean isSupport(String update) {
        try {
            Double.parseDouble(update);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }

    }

    private void sendMessage(Update update, Question question, InlineKeyboardMarkup markUp) {

        String correctedQuestion = "выбери действие с вопросом: \n" +
                "«" + question.getText() + "»";

        SendMessage msg = sendBotMessage.createMessageWithKeyboardMarkUpWithTextUpdate(update, correctedQuestion, markUp);
        sendBotMessage.sendMessage(msg);

        String nextQuestion = "Можете ввести следующий вопрос";
        SendMessage msg2 = sendBotMessage.createMessage(update, nextQuestion);
        sendBotMessage.sendMessage(msg2);
    }
}
