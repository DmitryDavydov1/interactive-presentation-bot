package com.example.bot._for_shelter.command.viewer;

import com.example.bot._for_shelter.command.Command;
import com.example.bot._for_shelter.command.SendBotMessage;
import com.example.bot._for_shelter.models.*;
import com.example.bot._for_shelter.repository.*;
import com.example.bot._for_shelter.service.HelpService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Component
public class AnswerTheQuestionCommand implements Command {
    private static final Pattern ANSWER_PATTERN = Pattern.compile("Отвечаю на вопрос (\\d+)");

    private final AnswerRepository answerRepository;
    private final ConditionRepository conditionRepository;
    private final RoomRepository roomRepository;
    private final SendBotMessage sendBotMessage;
    private final UserRepository userRepository;
    private final HelpService helpService;

    public AnswerTheQuestionCommand(AnswerRepository answerRepository, ConditionRepository conditionRepository,
                                    RoomRepository roomRepository,
                                    SendBotMessage sendBotMessage, UserRepository userRepository, HelpService helpService) {
        this.answerRepository = answerRepository;
        this.conditionRepository = conditionRepository;
        this.roomRepository = roomRepository;
        this.sendBotMessage = sendBotMessage;
        this.userRepository = userRepository;
        this.helpService = helpService;
    }

    @Override
    @Transactional
    public void execute(Update update) {
        String chatId = String.valueOf(update.getMessage().getChatId());
        String answerText = update.getMessage().getText();

        // Получение текущего состояния из базы
        Optional<Condition> conditionOpt = conditionRepository.findByChatId(chatId);
        if (conditionOpt.isEmpty()) return;

        Condition condition = conditionOpt.get();
        String[] conditionParts = condition.getCondition().split(" ");
        long roomId = Long.parseLong(conditionParts[3]);

        // Проверка, что комната существует и разрешено вводить ответы
        Optional<Room> roomOpt = roomRepository.findById(roomId);
        if (roomOpt.isEmpty() || !roomOpt.get().isAnswerStatus()) {
            sendBotMessage.sendMessage(sendBotMessage.createMessage(update, "Ответы больше нельзя вводить"));
            return;
        }

        List<Question> questions = helpService.getQuestionsByRoom(roomOpt.get());
        int currentQuestionIndex = Integer.parseInt(conditionParts[4]);

        // Сохранение ответа
        saveAnswer(chatId, currentQuestionIndex, questions, answerText);

        // Обновление индекса текущего вопроса и отправка следующего
        conditionParts[4] = String.valueOf(currentQuestionIndex + 1);
        condition.setCondition(String.join(" ", conditionParts));
        sendNextQuestionOrFinish(currentQuestionIndex, questions, update, condition);
    }

    @Override
    public boolean isSupport(String update) {
        return ANSWER_PATTERN.matcher(update).find();
    }

    // Метод для сохранения ответа
    private void saveAnswer(String chatId, int questionIndex, List<Question> questions, String answerText) {
        User user = userRepository.findByChatId(chatId).orElse(null);
        Question question = questions.get(questionIndex);

        Answer answer = new Answer();
        answer.setAnswer(answerText);
        answer.setQuestion(question);
        answer.setUser(user);
        answerRepository.save(answer);
    }

    // Метод для отправки следующего вопроса или завершения
    private void sendNextQuestionOrFinish(int currentQuestionIndex, List<Question> questions, Update update, Condition condition) {
        if (currentQuestionIndex >= questions.size() - 1) {
            sendBotMessage.sendMessage(sendBotMessage.createMessage(update, "Вопросы кончились"));
            condition.setCondition("Ответил на все вопросы");
        } else {
            Question nextQuestion = questions.get(currentQuestionIndex + 1);
            sendBotMessage.sendMessage(sendBotMessage.createMessage(update, "Ответь на этот вопрос: " + nextQuestion.getText()));
        }
        conditionRepository.save(condition);
    }
}
