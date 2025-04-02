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

        String statistics = generateStatistics(room);
        SendMessage message = sendBotMessage.createMessage(update, statistics);
        sendBotMessage.sendMessage(message);
    }

    private String generateStatistics(Room room) {
        List<Question> questions = room.getQuestions();
        List<Viewer> viewers = room.getViewers();
        List<Long> questionIds = getQuestionIds(questions);

        long completedCount = getViewerCountByCondition(viewers, questionIds, true);
        long inProgressCount = getViewerCountByCondition(viewers, questionIds, false);

        return String.format("Ответили до конца: %d\nСейчас отвечает: %d", completedCount, inProgressCount);
    }

    private List<Long> getQuestionIds(List<Question> questions) {
        return questions.stream()
                .map(Question::getId)
                .toList();
    }

    private long getViewerCountByCondition(List<Viewer> viewers, List<Long> questionIds, boolean isCompleted) {
        return viewers.stream()
                .filter(viewer -> {
                    int replies = answerRepository.numberReplies(questionIds, viewer.getId());
                    return isCompleted ? replies == questionIds.size() : replies > 0 && replies < questionIds.size();
                })
                .count();
    }

    @Override
    public boolean isSupport(String update) {
        return "Посмотреть статистику".equals(update);
    }
}
