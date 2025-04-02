package com.example.bot._for_shelter.command.room;

import com.example.bot._for_shelter.command.Command;
import com.example.bot._for_shelter.command.SendBotMessage;
import com.example.bot._for_shelter.models.CreatorTheRoom;
import com.example.bot._for_shelter.models.Question;
import com.example.bot._for_shelter.models.Room;
import com.example.bot._for_shelter.models.Viewer;
import com.example.bot._for_shelter.repository.CreatorTheRoomRepository;
import com.example.bot._for_shelter.service.HelpService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Component
public class SendStatisticCommand implements Command {
    private final CreatorTheRoomRepository creatorTheRoomRepository;
    private final HelpService helpService;
    private final SendBotMessage sendBotMessage;


    public SendStatisticCommand(CreatorTheRoomRepository creatorTheRoomRepository, HelpService helpService, SendBotMessage sendBotMessage) {
        this.creatorTheRoomRepository = creatorTheRoomRepository;
        this.helpService = helpService;
        this.sendBotMessage = sendBotMessage;
    }

    @Override
    @Transactional
    public void execute(Update update) {
        String chatId = update.getCallbackQuery().getMessage().getChatId().toString();

        Room roomWithStatusTrue = helpService.findLastRoomWithoutCashing(chatId);
        List<Question> questions = roomWithStatusTrue.getQuestions();
        List<Viewer> viewers = roomWithStatusTrue.getViewers();

        StringBuilder answer = new StringBuilder();
        for (Question question : questions) {

            //Получаем статистику по вопросу
            answer.append(question.getStatistic());
            String textMsg = answer.toString();
            if (textMsg.isEmpty()) {
                return;
            }
            //Отправляем статистику каждому гостю комнаты
            for (Viewer viewer : viewers) {
                SendMessage msg = sendBotMessage.sendMessageForAll(viewer.getChatId(), textMsg);
                sendBotMessage.sendMessage(msg);
            }
            SendMessage msg = sendBotMessage.createMessage(update, textMsg);
            sendBotMessage.sendMessage(msg);

            answer.setLength(0);
        }


    }

    @Override
    public boolean isSupport(String update) {
        return update.equals("Отправить статистику");
    }


}
