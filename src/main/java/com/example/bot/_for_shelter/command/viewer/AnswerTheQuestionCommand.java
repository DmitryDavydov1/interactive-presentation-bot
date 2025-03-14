package com.example.bot._for_shelter.command.viewer;

import com.example.bot._for_shelter.command.Command;
import com.example.bot._for_shelter.command.SendBotMessage;
import com.example.bot._for_shelter.models.*;
import com.example.bot._for_shelter.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class AnswerTheQuestionCommand implements Command {
    private final AnswerRepository answerRepository;
    private final ConditionRepository conditionRepository;
    private final ViewerRepository viewerRepository;
    private final RoomRepository roomRepository;
    private final SendBotMessage sendBotMessage;

    public AnswerTheQuestionCommand(AnswerRepository answerRepository, ConditionRepository conditionRepository,
                                    QuestionRepository questionRepository, ViewerRepository viewerRepository, ViewerRepository viewerRepository1,
                                    RoomRepository roomRepository, SendBotMessage sendBotMessage) {
        this.answerRepository = answerRepository;
        this.conditionRepository = conditionRepository;
        this.viewerRepository = viewerRepository1;
        this.roomRepository = roomRepository;
        this.sendBotMessage = sendBotMessage;
    }

    @Override
    @Transactional
    public void execute(Update update) {
        String text = update.getMessage().getText();
        String chatId = String.valueOf(update.getMessage().getChatId());

        Condition condition = conditionRepository.findByChatId(chatId);
        String[] conditionSplit = condition.getCondition().split(" ");
        int roomId = (int) Long.parseLong(conditionSplit[3]);

        List<Question> questions = roomRepository.findById(roomId).orElse(null).getQuestions();
        int questionId = (int) Long.parseLong(conditionSplit[4]);

        Viewer viewer = viewerRepository.findByChatId(chatId);
        Question question = questions.get(questionId);
        Answer answer = new Answer();
        answer.setQuestion(question);
        answer.setAnswer(text);
        answer.setViewer(viewer);
        answerRepository.save(answer);

        conditionSplit[4] = String.valueOf(questionId + 1);
        String newCondition = String.join(" ", conditionSplit);
        condition.setCondition(newCondition);
        conditionRepository.save(condition);


        if (questionId == questions.size() - 1) {
            String newTextForViewer = "Вопросы кончились";
            SendMessage message = sendBotMessage.createMessage(update, newTextForViewer);
            condition.setCondition("Ответил на все вопросы");
            sendBotMessage.sendMessage(message);
            conditionRepository.save(condition);
        } else {
            Question newQuestion = questions.get(questionId + 1);
            String newTextForViewer = "Ответь на этот вопрос " + newQuestion.getText();
            SendMessage message = sendBotMessage.createMessage(update, newTextForViewer);
            sendBotMessage.sendMessage(message);
        }
    }


    @Override
    public boolean isSupport(String update) {
        try {
            String regex = "Отвечаю на вопрос (\\d+)";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(update);

            return matcher.find();
        } catch (Exception e) {
            return false;
        }
    }
}
