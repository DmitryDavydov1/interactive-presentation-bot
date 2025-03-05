package com.example.bot._for_shelter.command.question;

import com.example.bot._for_shelter.command.Command;
import com.example.bot._for_shelter.command.SendBotMessage;
import com.example.bot._for_shelter.models.Question;
import com.example.bot._for_shelter.repository.QuestionRepository;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class DeleteQuestionCommand implements Command {

    private final QuestionRepository questionRepository;
    private final SendBotMessage sendBotMessage;

    public DeleteQuestionCommand(QuestionRepository questionRepository, SendBotMessage sendBotMessage) {
        this.questionRepository = questionRepository;
        this.sendBotMessage = sendBotMessage;
    }

    @Override
    public void execute(Update update) {
        String button = update.getCallbackQuery().getData();
        long id = Long.parseLong(button.split("-")[2]);

        Question question = questionRepository.findById(id).orElse(null);
        questionRepository.deleteById(id);


        String deleteMessage = "Вопрос удален " + "его текст: \n" + question.getText();
        SendMessage msg = sendBotMessage.createMessage(update, deleteMessage);


        int messageId = update.getCallbackQuery().getMessage().getMessageId();

        try {
            sendBotMessage.editMessage(msg, messageId);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

        SendMessage msg2 = sendBotMessage.createMessage(update, "Можете ввести следующий вопрос");
        sendBotMessage.sendMessage(msg2);

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
}
