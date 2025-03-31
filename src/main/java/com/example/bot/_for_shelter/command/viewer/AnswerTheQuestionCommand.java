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

        Condition condition = conditionRepository.findByChatId(chatId).orElse(null);
        String[] conditionSplit = condition.getCondition().split(" ");
        long roomId = Long.parseLong(conditionSplit[3]);
        Room room = roomRepository.findById(roomId).orElse(null);
        assert room != null;
        if (!room.isAnswerStatus()) {
            SendMessage message = sendBotMessage.createMessage(update, "Ответы больше нельзя вводить");
            sendBotMessage.sendMessage(message);
            return;
        }

        List<Question> questions = room.getQuestions();
        long questionId = Long.parseLong(conditionSplit[4]);
        Question question = questions.stream()
                .filter(q -> q.getId() == questionId)
                .findFirst()
                .orElse(null);
        
        if (question == null) {
            SendMessage message = sendBotMessage.createMessage(update, "Вопрос не найден");
            sendBotMessage.sendMessage(message);
            return;
        }

        Viewer viewer = viewerRepository.findByChatId(chatId);
        Answer answer = new Answer();
        answer.setQuestion(question);
        answer.setAnswer(text);
        answer.setViewer(viewer);
        answerRepository.save(answer);

        // Находим следующий вопрос
        Question nextQuestion = questions.stream()
                .filter(q -> q.getId() > questionId)
                .findFirst()
                .orElse(null);

        if (nextQuestion == null) {
            String newTextForViewer = "Вопросы кончились";
            SendMessage message = sendBotMessage.createMessage(update, newTextForViewer);
            condition.setCondition("Ответил на все вопросы");
            sendBotMessage.sendMessage(message);
            conditionRepository.save(condition);
        } else {
            conditionSplit[4] = String.valueOf(nextQuestion.getId());
            String newCondition = String.join(" ", conditionSplit);
            condition.setCondition(newCondition);
            conditionRepository.save(condition);

            String newTextForViewer = "Ответь на этот вопрос " + nextQuestion.getText();
            SendMessage message = sendBotMessage.createMessage(update, newTextForViewer);
            sendBotMessage.sendMessage(message);
        }
    }

    @Override
    public boolean isSupport(String update) {
        try {
            String regex = "Отвечаю на вопрос (\\d+) (\\d+)";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(update);

            if (matcher.find()) {
                long roomId = Long.parseLong(matcher.group(1));
                long questionId = Long.parseLong(matcher.group(2));
                return roomId > 0 && questionId > 0;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }
}

