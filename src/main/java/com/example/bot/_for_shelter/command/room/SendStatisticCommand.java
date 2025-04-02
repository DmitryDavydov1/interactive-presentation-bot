package com.example.bot._for_shelter.command.room;

import com.example.bot._for_shelter.command.Command;
import com.example.bot._for_shelter.command.SendBotMessage;
import com.example.bot._for_shelter.models.Question;
import com.example.bot._for_shelter.models.Room;
import com.example.bot._for_shelter.models.Viewer;
import com.example.bot._for_shelter.service.HelpService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Component
public class SendStatisticCommand implements Command {
    private final HelpService helpService;
    private final SendBotMessage sendBotMessage;

    public SendStatisticCommand(HelpService helpService, SendBotMessage sendBotMessage) {
        this.helpService = helpService;
        this.sendBotMessage = sendBotMessage;
    }

    @Override
    @Transactional
    public void execute(Update update) {
        String chatId = String.valueOf(update.getCallbackQuery().getMessage().getChatId());

        Room room = helpService.findLastRoomWithoutCashing(chatId);
        List<Question> questions = room.getQuestions();
        List<Viewer> viewers = room.getViewers();

        StringBuilder statisticsMessage = new StringBuilder();
        for (Question question : questions) {
            String questionStats = question.getStatistic();
            statisticsMessage.append(questionStats);

            if (!statisticsMessage.isEmpty()) {
                // Отправляем статистику каждому зрителю
                for (Viewer viewer : viewers) {
                    sendBotMessage.sendMessage(sendBotMessage.sendMessageForAll(viewer.getChatId(), statisticsMessage.toString()));
                }

                // Отправляем статистику создателю
                sendBotMessage.sendMessage(sendBotMessage.createMessage(update, statisticsMessage.toString()));
            }

            statisticsMessage.setLength(0); // Сбросить StringBuilder для следующего вопроса
        }
    }

    @Override
    public boolean isSupport(String update) {
        return "Отправить статистику".equals(update);
    }
}
