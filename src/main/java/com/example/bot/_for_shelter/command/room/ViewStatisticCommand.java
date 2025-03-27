package com.example.bot._for_shelter.command.room;

import com.example.bot._for_shelter.command.Command;
import com.example.bot._for_shelter.command.SendBotMessage;
import com.example.bot._for_shelter.models.*;
import com.example.bot._for_shelter.repository.AnswerRepository;
import com.example.bot._for_shelter.repository.CreatorTheRoomRepository;
import com.example.bot._for_shelter.service.HelpService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;


@Component
public class ViewStatisticCommand implements Command {

    private final CreatorTheRoomRepository creatorTheRoomRepository;
    private final HelpService helpService;
    private final AnswerRepository answerRepository;
    private final SendBotMessage sendBotMessage;

    public ViewStatisticCommand(CreatorTheRoomRepository creatorTheRoomRepository, HelpService helpService, AnswerRepository answerRepository, SendBotMessage sendBotMessage) {
        this.creatorTheRoomRepository = creatorTheRoomRepository;
        this.helpService = helpService;
        this.answerRepository = answerRepository;
        this.sendBotMessage = sendBotMessage;
    }

    @Override
    @Transactional
    public void execute(Update update) {
        String chatId = update.getCallbackQuery().getMessage().getChatId().toString();
        CreatorTheRoom creatorTheRoom = creatorTheRoomRepository.findByChatId(chatId);
        Room room = helpService.findLastRoom(creatorTheRoom);

        List<Question> questions = room.getQuestions();
        List<Viewer> viewers = room.getViewers();

        int count = 0;
        int respondingRightNow = 0;
        List<Integer> questionIds = questions.stream().map(Question::getId).toList();
        for (Viewer viewer : viewers) {
            int numberOfReplies = answerRepository.numberReplies(questionIds, viewer.getId());
            if (questions.size() == numberOfReplies) {
                count += 1;
            } else if (numberOfReplies >= 1) {
                respondingRightNow += 1;
            }
        }


        String statistic = "Ответити до конца: " + count + "\nСейчас отвечает " + respondingRightNow;
        SendMessage sendMessage = sendBotMessage.createMessage(update, statistic);
        sendBotMessage.sendMessage(sendMessage);


    }

    @Override
    public boolean isSupport(String update) {
        return update.equals("Посмотреть статистику");
    }
}
