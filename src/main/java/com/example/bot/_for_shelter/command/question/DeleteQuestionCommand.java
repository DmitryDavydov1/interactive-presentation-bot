package com.example.bot._for_shelter.command.question;

import com.example.bot._for_shelter.command.Command;
import com.example.bot._for_shelter.command.SendBotMessage;
import com.example.bot._for_shelter.models.Condition;
import com.example.bot._for_shelter.models.CreatorTheRoom;
import com.example.bot._for_shelter.models.Question;
import com.example.bot._for_shelter.models.Room;
import com.example.bot._for_shelter.repository.ConditionRepository;
import com.example.bot._for_shelter.repository.CreatorTheRoomRepository;
import com.example.bot._for_shelter.repository.QuestionRepository;
import com.example.bot._for_shelter.service.HelpService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class DeleteQuestionCommand implements Command {

    private final QuestionRepository questionRepository;
    private final SendBotMessage sendBotMessage;
    private final CreatorTheRoomRepository creatorTheRoomRepository;
    private final HelpService helpService;
    private final ConditionRepository conditionRepository;

    public DeleteQuestionCommand(QuestionRepository questionRepository, SendBotMessage sendBotMessage, CreatorTheRoomRepository creatorTheRoomRepository, HelpService helpService, ConditionRepository conditionRepository) {
        this.questionRepository = questionRepository;
        this.sendBotMessage = sendBotMessage;
        this.creatorTheRoomRepository = creatorTheRoomRepository;
        this.helpService = helpService;
        this.conditionRepository = conditionRepository;
    }

    @Override
    @Transactional
    public void execute(Update update) {
        String chatId = String.valueOf(update.getCallbackQuery().getFrom().getId());
        CreatorTheRoom creatorTheRoom = creatorTheRoomRepository.findByChatId(chatId);
        Room room = helpService.findLastRoom(creatorTheRoom);
        if (!room.isQuestionStatus()) {
            SendMessage msg = sendBotMessage.createMessage(update, "Вы уже заврешили редактирование комнаты");
            sendBotMessage.sendMessage(msg);
            return;
        }
        Condition condition = conditionRepository.findByChatId(chatId).orElse(null);

        Integer parts = Integer.valueOf(condition.getCondition().split(" ")[2]);

        sendBotMessage.deleteMessageWithMessageId(update, parts);
        sendBotMessage.deleteMessage(update);
        String button = update.getCallbackQuery().getData();

        long id = Long.parseLong(button.split("-")[2]);
        questionRepository.deleteById(id);


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

//    private void sendMessage(Update update, Question question) {
//        String deleteMessage = "Вопрос удален " + "его текст: \n" + question.getText();
//        SendMessage msg = sendBotMessage.createMessage(update, deleteMessage);
//        SendMessage msg2 = sendBotMessage.createMessage(update, "Можете ввести следующий вопрос");
//        sendBotMessage.sendMessage(msg);
//        sendBotMessage.sendMessage(msg2);
//    }
}
