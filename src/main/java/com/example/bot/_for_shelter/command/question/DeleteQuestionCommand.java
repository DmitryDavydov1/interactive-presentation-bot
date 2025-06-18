package com.example.bot._for_shelter.command.question;

import com.example.bot._for_shelter.command.Command;
import com.example.bot._for_shelter.command.SendBotMessage;
import com.example.bot._for_shelter.models.Room;
import com.example.bot._for_shelter.repository.QuestionRepository;
import com.example.bot._for_shelter.service.HelpService;
import jakarta.transaction.Transactional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class DeleteQuestionCommand implements Command {

    private final QuestionRepository questionRepository;
    private final SendBotMessage sendBotMessage;
    private final HelpService helpService;

    public DeleteQuestionCommand(QuestionRepository questionRepository, SendBotMessage sendBotMessage, HelpService helpService) {
        this.questionRepository = questionRepository;
        this.sendBotMessage = sendBotMessage;
        this.helpService = helpService;
    }

    @Override
    @Transactional
    public void execute(Update update) {
        String[] parts = update.getCallbackQuery().getData().split("-");
        String chatId = String.valueOf(update.getCallbackQuery().getFrom().getId());

        // Проверка на наличие активной комнаты с возможностью редактирования вопросов
        Room room = helpService.findLastRoomWithoutCashing(chatId);
        if (room == null || !room.isQuestionStatus()) {
            sendErrorMessage(update, "Редактирование вопросов завершено или комната не найдена.");
            return;
        }

        // Извлечение ID вопроса и его удаление
        long questionId = Long.parseLong(parts[2]);
        deleteQuestion(questionId, room);

        // Удаление сообщения
        Integer messageId = Integer.valueOf(parts[3]);
        deleteMessages(update, messageId);


    }

    @Override
    public boolean isSupport(String update) {
        try {
            String[] parts = update.split("-");
            return parts[0].equals("delete");
        } catch (Exception e) {
            return false;
        }
    }

    @CacheEvict(value = "question", key = "#room.id")
    public void deleteQuestion(long questionId, Room room) {
        questionRepository.deleteById(questionId);
    }

    // Метод для отправки сообщения об ошибке
    private void sendErrorMessage(Update update, String errorMessage) {
        SendMessage msg = sendBotMessage.createMessage(update, errorMessage);
        sendBotMessage.sendMessage(msg);
    }


    // Метод для удаления сообщений
    public void deleteMessages(Update update, Integer messageId) {
        sendBotMessage.deleteMessageWithMessageId(update, messageId);
        sendBotMessage.deleteMessage(update);
    }
}
