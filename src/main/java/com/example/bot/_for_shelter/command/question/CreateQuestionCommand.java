package com.example.bot._for_shelter.command.question;

import com.example.bot._for_shelter.command.Command;
import com.example.bot._for_shelter.command.SendBotMessage;
import com.example.bot._for_shelter.mark_ups.MarkUps;
import com.example.bot._for_shelter.models.CreatorTheRoom;
import com.example.bot._for_shelter.models.Question;
import com.example.bot._for_shelter.models.Room;
import com.example.bot._for_shelter.repository.CreatorTheRoomRepository;
import com.example.bot._for_shelter.repository.QuestionRepository;
import com.example.bot._for_shelter.service.HelpService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;


@Component
public class CreateQuestionCommand implements Command {

    private final CreatorTheRoomRepository creatorTheRoomRepository;
    private final QuestionRepository questionRepository;
    private final MarkUps markUps;
    private final SendBotMessage sendBotMessage;
    private final HelpService helpService;


    public CreateQuestionCommand(CreatorTheRoomRepository creatorTheRoomRepository, QuestionRepository questionRepository, MarkUps markUps,
                                 SendBotMessage sendBotMessage, HelpService helpService) {
        this.creatorTheRoomRepository = creatorTheRoomRepository;
        this.questionRepository = questionRepository;
        this.markUps = markUps;
        this.sendBotMessage = sendBotMessage;
        this.helpService = helpService;
    }

    @Override
    @Transactional
    public void execute(Update update) {
        String chatId = String.valueOf(update.getMessage().getChatId());
        CreatorTheRoom creatorTheRoom = creatorTheRoomRepository.findByChatId(chatId);
        Room roomWithStatusTrue = helpService.findLastRoom(creatorTheRoom);


        assert roomWithStatusTrue != null;
        Question question = new Question();
        question.setRoom(roomWithStatusTrue);
        question.setText(update.getMessage().getText());

        questionRepository.save(question);
        sendMessage(update, question);
    }

    @Override
    public boolean isSupport(String update) {
        return update.startsWith("Добавляю запросы");
    }

    private void sendMessage(Update update, Question question) {
        String correctedQuestion = "выбери действие с вопросом: \n" +
                "«" + question.getText() + "»";
        long questionId = question.getId();
        InlineKeyboardMarkup markUp = markUps.questionActivitiesButton(questionId, update);
        SendMessage msg = sendBotMessage.createMessageWithKeyboardMarkUpWithTextUpdate(update, correctedQuestion, markUp);


        sendBotMessage.sendMessage(msg);
    }
}
