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

        String answerText = update.getMessage().getText();
        String chatId = String.valueOf(update.getMessage().getChatId());

        Condition condition = conditionRepository.findByChatId(chatId).orElse(null);
        String[] conditionSplit = condition.getCondition().split(" ");

        long roomId = Long.parseLong(conditionSplit[3]);
        Room room = roomRepository.findById(roomId).orElse(null);

        assert room != null;
        //Проверяем принимает ли комната ответы
        if (!room.isAnswerStatus()) {
            SendMessage message = sendBotMessage.createMessage(update, "Ответы больше нельзя вводить");
            sendBotMessage.sendMessage(message);
            return;
        }

        //Получаем список вопросов в комнате
        List<Question> questions = room.getQuestions();

        //Получаем индекс вопроса в списке вопросов
        int questionIndex = Integer.parseInt(conditionSplit[4]);

        saveAnswer(chatId, questionIndex, questions, answerText);

        //Указываем индекс следующего вопроса в списке вопросов
        conditionSplit[4] = String.valueOf(questionIndex + 1);

        //Обновляем condition
        String newCondition = String.join(" ", conditionSplit);
        condition.setCondition(newCondition);

        //Спрашиваем следующий вопрос или сообщаем что вопросы кончились
        sendMessage(questionIndex, questions, update, condition);
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


    private void saveAnswer(String chatId, int questionIndex, List<Question> questionList, String answerText) {
        //Находим отвечающего
        Viewer viewer = viewerRepository.findByChatId(chatId);

        //Находим вопрос по его индексу в списке вопросов
        Question question = questionList.get(questionIndex);

        Answer answer = new Answer();
        answer.setQuestion(question);
        answer.setAnswer(answerText);
        answer.setViewer(viewer);
        answerRepository.save(answer);
    }

    private void sendMessage(int questionIndex, List<Question> questions, Update update, Condition condition) {
        if (questionIndex == questions.size() - 1) {
            String newTextForViewer = "Вопросы кончились";
            SendMessage message = sendBotMessage.createMessage(update, newTextForViewer);
            condition.setCondition("Ответил на все вопросы");
            sendBotMessage.sendMessage(message);
            conditionRepository.save(condition);

        } else {
            conditionRepository.save(condition);
            Question newQuestion = questions.get(questionIndex + 1);
            String newTextForViewer = "Ответь на этот вопрос " + newQuestion.getText();
            SendMessage message = sendBotMessage.createMessage(update, newTextForViewer);
            sendBotMessage.sendMessage(message);
        }
    }
}