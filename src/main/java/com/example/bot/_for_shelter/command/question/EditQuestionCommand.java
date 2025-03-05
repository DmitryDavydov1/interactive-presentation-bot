package com.example.bot._for_shelter.command.question;

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
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.List;

@Component
public class EditQuestionCommand implements Command {

    private final QuestionRepository questionRepository;
    private final CreatorTheRoomRepository creatorTheRoomRepository;
    private final MarkUps markUps;
    private final SendBotMessage sendBotMessage;

    public EditQuestionCommand(QuestionRepository questionRepository, CreatorTheRoomRepository creatorTheRoomRepository, MarkUps markUps, SendBotMessage sendBotMessage) {
        this.questionRepository = questionRepository;
        this.creatorTheRoomRepository = creatorTheRoomRepository;
        this.markUps = markUps;
        this.sendBotMessage = sendBotMessage;
    }

    @Override
    public void execute(Update update) {
        String textMessage = update.getMessage().getText();
        String chatId = String.valueOf(update.getMessage().getChatId());
        CreatorTheRoom creatorTheRoom = creatorTheRoomRepository.findByChatId(chatId);
        List<Room> rooms = creatorTheRoom.getRoom();


        Room roomWithStatusTrue = rooms.stream()
                .filter(Room::isStatus) // Фильтруем по статусу
                .findFirst().orElse(null);


        Long questionId = Long.parseLong(roomWithStatusTrue.getEditQuestionStatus());

        Question question = questionRepository.findById(questionId).orElse(null);
        question.setText(textMessage);
        questionRepository.save(question);


        String correctedQuestion = "выбери действие с вопросом: \n" +
                "«" + question.getText() + "»";
        InlineKeyboardMarkup markUp = markUps.questionActivitiesButton(questionId);
        SendMessage msg = sendBotMessage.createMessageWithKeyboardMarkUp(update, correctedQuestion, markUp);
        sendBotMessage.sendMessage(msg);


        String nextQuestion = "Можете ввести следующий вопрос";
        SendMessage msg2 = sendBotMessage.createMessage(update, nextQuestion);
        sendBotMessage.sendMessage(msg2);
    }

    @Override
    public boolean isSupport(String update) {
        return false;
    }
}
