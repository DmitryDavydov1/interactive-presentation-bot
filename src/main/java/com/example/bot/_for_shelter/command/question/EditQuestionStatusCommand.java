package com.example.bot._for_shelter.command.question;

import com.example.bot._for_shelter.command.Command;
import com.example.bot._for_shelter.command.SendBotMessage;
import com.example.bot._for_shelter.models.Condition;
import com.example.bot._for_shelter.models.Question;
import com.example.bot._for_shelter.models.Room;
import com.example.bot._for_shelter.repository.ConditionRepository;
import com.example.bot._for_shelter.repository.QuestionRepository;
import com.example.bot._for_shelter.service.HelpService;
import com.example.bot._for_shelter.service.TelegramBot;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class EditQuestionStatusCommand implements Command {

    private final SendBotMessage sendBotMessage;
    private final QuestionRepository questionRepository;
    private final ConditionRepository conditionRepository;
    private final HelpService helpService;
    private final TelegramBot telegramBot;

    @Autowired
    @Lazy
    public EditQuestionStatusCommand(SendBotMessage sendBotMessage,
                                     QuestionRepository questionRepository,
                                     ConditionRepository conditionRepository,
                                     HelpService helpService,
                                     TelegramBot telegramBot) {
        this.sendBotMessage = sendBotMessage;
        this.questionRepository = questionRepository;
        this.conditionRepository = conditionRepository;
        this.helpService = helpService;
        this.telegramBot = telegramBot;
    }

    @Override
    @Transactional
    public void execute(Update update) {
        String updateMessage = update.getCallbackQuery().getData();
        String chatId = String.valueOf(update.getCallbackQuery().getFrom().getId());
        Integer updateMessageId = update.getCallbackQuery().getMessage().getMessageId();

        Room room = helpService.findLastRoom(chatId);
        if (room == null || !room.isQuestionStatus()) {
            sendBotMessageWithText(update, "Вы уже завершили редактирование комнаты.");
            return;
        }

        String[] parts = updateMessage.split("-");
        if (parts.length < 4) {
            sendBotMessageWithText(update, "Неверные данные для редактирования вопроса.");
            return;
        }

        Condition condition = conditionRepository.findByChatId(chatId).orElse(null);
        if (condition == null) {
            sendBotMessageWithText(update, "Не найдено состояние для текущего чата.");
            return;
        }

        try {
            int messageId = sendMessage(update, Long.parseLong(parts[2]));
            String newCondition = buildNewCondition(parts, updateMessageId, messageId);
            condition.setCondition(newCondition);
            conditionRepository.save(condition);
        } catch (TelegramApiException e) {
            sendBotMessageWithText(update, "Ошибка при отправке сообщения: " + e.getMessage());
        } catch (NumberFormatException e) {
            sendBotMessageWithText(update, "Некорректные данные для обработки.");
        }
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

    private void sendBotMessageWithText(Update update, String text) {
        SendMessage msg = sendBotMessage.createMessage(update, text);
        sendBotMessage.sendMessage(msg);
    }

    private String buildNewCondition(String[] parts, Integer updateMessageId, int messageId) {
        return "Изменяю-вопрос-" + parts[2] + "-" + updateMessageId + "-" + parts[3] + "-" + messageId;
    }

    private int sendMessage(Update update, long questionId) throws TelegramApiException {
        Question question = questionRepository.findById(questionId).orElse(null);
        if (question == null) {
            throw new RuntimeException("Вопрос не найден для ID: " + questionId);
        }

        SendMessage sendMessage = sendBotMessage.createMessage(update, "Можешь ввести исправленный текст для вопроса: \n" +
                "«" + question.getText() + "»");

        return telegramBot.sendMessage(sendMessage);
    }
}
