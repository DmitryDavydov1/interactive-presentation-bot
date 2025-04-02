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
import java.util.Optional;
import java.util.regex.Pattern;

@Component
public class AnswerTheQuestionCommand implements Command {
    private static final Pattern ANSWER_PATTERN = Pattern.compile("Отвечаю на вопрос (\\d+)");

    private final AnswerRepository answerRepository;
    private final ConditionRepository conditionRepository;
    private final ViewerRepository viewerRepository;
    private final RoomRepository roomRepository;
    private final SendBotMessage sendBotMessage;

    public AnswerTheQuestionCommand(AnswerRepository answerRepository, ConditionRepository conditionRepository,
                                    ViewerRepository viewerRepository, RoomRepository roomRepository,
                                    SendBotMessage sendBotMessage) {
        this.answerRepository = answerRepository;
        this.conditionRepository = conditionRepository;
        this.viewerRepository = viewerRepository;
        this.roomRepository = roomRepository;
        this.sendBotMessage = sendBotMessage;
    }

    @Override
    @Transactional
    public void execute(Update update) {
        String chatId = String.valueOf(update.getMessage().getChatId());
        String answerText = update.getMessage().getText();

        Condition condition = conditionRepository.findByChatId(chatId).orElse(null);
        if (condition == null) return;

        String[] conditionSplit = condition.getCondition().split(" ");
        long roomId = Long.parseLong(conditionSplit[3]);
        Room room = roomRepository.findById(roomId).orElse(null);
        if (room == null || !room.isAnswerStatus()) {
            sendBotMessage.sendMessage(sendBotMessage.createMessage(update, "Ответы больше нельзя вводить"));
            return;
        }

        List<Question> questions = room.getQuestions();
        int questionIndex = Integer.parseInt(conditionSplit[4]);
        saveAnswer(chatId, questionIndex, questions, answerText);

        conditionSplit[4] = String.valueOf(questionIndex + 1);
        condition.setCondition(String.join(" ", conditionSplit));
        sendNextQuestionOrFinish(questionIndex, questions, update, condition);
    }

    @Override
    public boolean isSupport(String update) {
        return ANSWER_PATTERN.matcher(update).find();
    }

    private void saveAnswer(String chatId, int questionIndex, List<Question> questionList, String answerText) {
        Viewer viewer = viewerRepository.findByChatId(chatId);
        Question question = questionList.get(questionIndex);

        Answer answer = new Answer();
        answer.setAnswer(answerText);
        answer.setQuestion(question);
        answer.setViewer(viewer);
        answerRepository.save(answer);
    }

    private void sendNextQuestionOrFinish(int questionIndex, List<Question> questions, Update update, Condition condition) {
        if (questionIndex >= questions.size() - 1) {
            sendBotMessage.sendMessage(sendBotMessage.createMessage(update, "Вопросы кончились"));
            condition.setCondition("Ответил на все вопросы");
        } else {
            Question nextQuestion = questions.get(questionIndex + 1);
            sendBotMessage.sendMessage(sendBotMessage.createMessage(update, "Ответь на этот вопрос: " + nextQuestion.getText()));
        }
        conditionRepository.save(condition);
    }
}