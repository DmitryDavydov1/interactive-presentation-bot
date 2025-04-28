package com.example.bot._for_shelter.command.question;

import com.example.bot._for_shelter.command.Command;
import com.example.bot._for_shelter.command.SendBotMessage;
import com.example.bot._for_shelter.mark_ups.MarkUps;
import com.example.bot._for_shelter.models.Question;
import com.example.bot._for_shelter.models.Room;
import com.example.bot._for_shelter.repository.QuestionRepository;
import com.example.bot._for_shelter.service.HelpService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;


import java.util.Optional;

@Component
public class CreateQuestionCommand implements Command {

    private final HelpService helpService;
    private final QuestionRepository questionRepository;
    private final MarkUps markUps;
    private final SendBotMessage sendBotMessage;
    private static final Logger logger = LoggerFactory.getLogger(CreateQuestionCommand.class);


    @Autowired
    public CreateQuestionCommand(HelpService helpService, QuestionRepository questionRepository, MarkUps markUps,
                                 SendBotMessage sendBotMessage) {
        this.helpService = helpService;
        this.questionRepository = questionRepository;
        this.markUps = markUps;
        this.sendBotMessage = sendBotMessage;
    }

    @Override
    @Transactional
    public void execute(Update update) {
        String chatId = String.valueOf(update.getMessage().getChatId());
        int length = update.getMessage().getText().length();
        if (length > 100) {
            SendMessage sendMessage = sendBotMessage.createMessage(update, "Твой вопрос слишком длинный");
            sendBotMessage.sendMessage(sendMessage);

            logger.warn("Слишком длинный вопрос от юзера: {}, длинною: {}", chatId, length);
            return;
        }

        // Получаем комнату с активным статусом
        Optional<Room> roomWithStatusTrue = Optional.ofNullable(helpService.findLastRoom(chatId));

        if (roomWithStatusTrue.isEmpty()) {
            // Возвращаем ошибку, если комната не найдена
            SendMessage sendMessage = sendBotMessage.sendMessageForAll(chatId, "Не найдена активная комната.");
            sendBotMessage.sendMessage(sendMessage);
            return;
        }

        // Создание нового вопроса
        Question question = createQuestion(update, roomWithStatusTrue.get());

        // Сохранение вопроса в репозитории
        questionRepository.save(question);

        // Отправка сообщения с кнопками
        sendMessage(update, question);
    }

    @Override
    public boolean isSupport(String update) {
        return update.startsWith("Добавляю запросы");
    }

    @CacheEvict(value = "question", key = "#room.id")
    public Question createQuestion(Update update, Room room) {
        Question question = new Question();
        question.setRoom(room);
        question.setText(update.getMessage().getText());
        return question;
    }

    private void sendMessage(Update update, Question question) {
        String messageText = buildMessageText(question);
        InlineKeyboardMarkup markUp = markUps.questionActivitiesButton(question.getId(), update);
        SendMessage msg = sendBotMessage.createMessageWithKeyboardMarkUpWithTextUpdate(update, messageText, markUp);
        sendBotMessage.sendMessage(msg);
    }

    private String buildMessageText(Question question) {
        return "Выберите действие с вопросом: \n" +
                "«" + question.getText() + "»";
    }
}
