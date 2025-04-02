package com.example.bot._for_shelter.command.room;

import com.example.bot._for_shelter.command.Command;
import com.example.bot._for_shelter.command.SendBotMessage;
import com.example.bot._for_shelter.models.*;
import com.example.bot._for_shelter.repository.AnswerRepository;
import com.example.bot._for_shelter.service.HelpService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Component
public class ViewStatisticCommand implements Command {

    private final HelpService helpService;
    private final AnswerRepository answerRepository;
    private final SendBotMessage sendBotMessage;

    public ViewStatisticCommand(HelpService helpService, AnswerRepository answerRepository, SendBotMessage sendBotMessage) {
        this.helpService = helpService;
        this.answerRepository = answerRepository;
        this.sendBotMessage = sendBotMessage;
    }

    @Override
    @Transactional
    public void execute(Update update) {
        String chatId = update.getCallbackQuery().getMessage().getChatId().toString();
        Room room = helpService.findLastRoomWithoutCashing(chatId);

        String statisticMessage = generateStatistics(room);
        SendMessage sendMessage = sendBotMessage.createMessage(update, statisticMessage);
        sendBotMessage.sendMessage(sendMessage);
    }

    private String generateStatistics(Room room) {
        List<Question> questions = room.getQuestions();
        List<Viewer> viewers = room.getViewers();
        List<Long> questionIds = questions.stream().map(Question::getId).toList();

        long completed = viewers.stream()
                .filter(viewer -> answerRepository.numberReplies(questionIds, viewer.getId()) == questions.size())
                .count();

        long inProgress = viewers.stream()
                .filter(viewer -> {
                    int replies = answerRepository.numberReplies(questionIds, viewer.getId());
                    return replies > 0 && replies < questions.size();
                })
                .count();

        return String.format("Ответили до конца: %d\nСейчас отвечает: %d", completed, inProgress);
    }

    @Override
    public boolean isSupport(String update) {
        return "Посмотреть статистику".equals(update);
    }
}
