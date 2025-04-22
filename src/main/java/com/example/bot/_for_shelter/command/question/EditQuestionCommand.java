package com.example.bot._for_shelter.command.question;

import com.example.bot._for_shelter.command.Command;
import com.example.bot._for_shelter.command.SendBotMessage;
import com.example.bot._for_shelter.mark_ups.MarkUps;
import com.example.bot._for_shelter.models.Condition;
import com.example.bot._for_shelter.models.Question;
import com.example.bot._for_shelter.models.Room;
import com.example.bot._for_shelter.repository.ConditionRepository;
import com.example.bot._for_shelter.repository.QuestionRepository;
import com.example.bot._for_shelter.service.HelpService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.logging.Handler;

@Component
public class EditQuestionCommand implements Command {

    private final QuestionRepository questionRepository;
    private final MarkUps markUps;
    private final SendBotMessage sendBotMessage;
    private final ConditionRepository conditionRepository;
    private final HelpService helpService;

    public EditQuestionCommand(QuestionRepository questionRepository, MarkUps markUps, SendBotMessage sendBotMessage,
                               ConditionRepository conditionRepository, HelpService helpService) {
        this.questionRepository = questionRepository;
        this.markUps = markUps;
        this.sendBotMessage = sendBotMessage;
        this.conditionRepository = conditionRepository;
        this.helpService = helpService;
    }

    @Override
    public void execute(Update update) {
        String chatId = String.valueOf(update.getMessage().getChatId());
        Room room = helpService.findLastRoom(chatId);


        // Получение условия
        Condition condition = conditionRepository.findByChatId(chatId).orElse(null);
        if (condition == null) {
            sendErrorMessage(update, "Не найдено активное состояние для вашего чата.");
            return;
        }

        // Разбор условия и обновление вопроса
        String[] parts = condition.getCondition().split("-");
        Question question = questionRepository.findById(Long.valueOf(parts[2])).orElse(null);
        if (question == null) {
            sendErrorMessage(update, "Вопрос с таким ID не найден.");
            return;
        }

        // Обновление текста вопроса
        editQuestion(update, question, room);

        // Создание клавиатуры для действий с вопросом
        InlineKeyboardMarkup markUp = markUps.questionActivitiesButton(Long.parseLong(parts[2]), update);

        // Обновление состояния
        condition.setCondition("Добавляю запросы");
        conditionRepository.save(condition);

        // Удаление предыдущих сообщений
        deleteMessages(update, parts);

        // Отправка обновленного вопроса
        sendMessage(update, question, markUp);
    }

    @Override
    public boolean isSupport(String update) {
        try {
            String[] parts = update.split("-");
            return parts[0].equals("Изменяю");
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // Метод для отправки сообщения об ошибке
    private void sendErrorMessage(Update update, String errorMessage) {
        SendMessage msg = sendBotMessage.createMessage(update, errorMessage);
        sendBotMessage.sendMessage(msg);
    }

    // Метод для отправки сообщения с обновленным вопросом
    private void sendMessage(Update update, Question question, InlineKeyboardMarkup markUp) {
        String correctedQuestion = "Выберите действие с вопросом: \n" +
                "«" + question.getText() + "»";
        SendMessage msg = sendBotMessage.createMessageWithKeyboardMarkUpWithTextUpdate(update, correctedQuestion, markUp);
        sendBotMessage.sendMessage(msg);
    }

    // Метод для удаления сообщений
    private void deleteMessages(Update update, String[] parts) {
        try {
            sendBotMessage.deleteMessageWithMessageId(update, Integer.valueOf(parts[3]));
            sendBotMessage.deleteMessageWithMessageId(update, Integer.valueOf(parts[4]));
            sendBotMessage.deleteMessageWithMessageId(update, Integer.valueOf(parts[5]));
        } catch (NumberFormatException e) {
            // Если не удается преобразовать id сообщений, логируем ошибку
            System.err.println("Ошибка при удалении сообщений: " + e.getMessage());
        }
    }

    @CacheEvict(value = "question", key = "#room.id")
    public void editQuestion(Update update, Question question, Room room) {
        question.setText(update.getMessage().getText());
        questionRepository.save(question);
    }
}
