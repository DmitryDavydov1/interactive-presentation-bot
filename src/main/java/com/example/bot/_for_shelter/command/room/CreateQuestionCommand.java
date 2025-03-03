package com.example.bot._for_shelter.command.room;

import com.example.bot._for_shelter.command.Command;
import com.example.bot._for_shelter.command.SendBotMessage;
import com.example.bot._for_shelter.mark_ups.MarkUps;
import com.example.bot._for_shelter.models.CreatorTheRoom;
import com.example.bot._for_shelter.models.Question;
import com.example.bot._for_shelter.models.Room;
import com.example.bot._for_shelter.repository.CreatorTheRoomRepository;
import com.example.bot._for_shelter.repository.QuestionRepository;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Component
public class CreateQuestionCommand implements Command {

    private final CreatorTheRoomRepository creatorTheRoomRepository;
    private final QuestionRepository questionRepository;
    private final MarkUps markUps;
    private final SendBotMessage sendBotMessage;

    public CreateQuestionCommand(CreatorTheRoomRepository creatorTheRoomRepository, QuestionRepository questionRepository, MarkUps markUps, SendBotMessage sendBotMessage) {
        this.creatorTheRoomRepository = creatorTheRoomRepository;
        this.questionRepository = questionRepository;
        this.markUps = markUps;
        this.sendBotMessage = sendBotMessage;
    }

    @Override
    public void execute(Update update) {
        String chatId = String.valueOf(update.getMessage().getChatId());
        CreatorTheRoom creatorTheRoom = creatorTheRoomRepository.findByChatId(chatId);
        List<Room> rooms = creatorTheRoom.getRoom();
        Room roomWithStatusTrue = rooms.stream()
                .filter(Room::isStatus) // Фильтруем по статусу
                .findFirst().orElse(null);
        assert roomWithStatusTrue != null;


        Question question = new Question();
        question.setRoom(roomWithStatusTrue);
        question.setText(update.getMessage().getText());
        questionRepository.save(question);
        SendMessage msg = new SendMessage();
        msg.setReplyMarkup(markUps.questionActivitiesButton(question.getId()));
        msg.setChatId(chatId);
        msg.setText("выбери действие с вопросом");
        sendBotMessage.sendMessage(msg);

    }

    @Override
    public boolean isSupport(String update) {
        return false;
    }
}
