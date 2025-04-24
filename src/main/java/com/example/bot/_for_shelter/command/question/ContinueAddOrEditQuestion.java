package com.example.bot._for_shelter.command.question;

import com.example.bot._for_shelter.command.Command;
import com.example.bot._for_shelter.command.SendBotMessage;

import com.example.bot._for_shelter.models.Condition;

import com.example.bot._for_shelter.models.Room;
import com.example.bot._for_shelter.repository.ConditionRepository;

import com.example.bot._for_shelter.repository.RoomRepository;
import com.example.bot._for_shelter.service.HelpService;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;


@Component
public class ContinueAddOrEditQuestion implements Command {

    private final HelpService helpService;
    private final RoomRepository roomRepository;
    private final ConditionRepository conditionRepository;
    private final SendBotMessage sendBotMessage;

    public ContinueAddOrEditQuestion(HelpService helpService, RoomRepository roomRepository, ConditionRepository conditionRepository, SendBotMessage sendBotMessage) {
        this.helpService = helpService;
        this.roomRepository = roomRepository;
        this.conditionRepository = conditionRepository;
        this.sendBotMessage = sendBotMessage;
    }


    @Override
    public void execute(Update update) {
        String chatId = String.valueOf(update.getCallbackQuery().getMessage().getChatId());
        Condition condition = conditionRepository.findByChatId(chatId).orElse(null);


        Room room = helpService.findLastRoomWithoutCashing(chatId);
        assert condition != null;
        condition.setCondition("Добавляю запросы");
        room.setQuestionStatus(true);
        conditionRepository.save(condition);
        roomRepository.save(room);

        sendRoomCreatedMessage(update);
    }

    @Override
    public boolean isSupport(String update) {
        return update.equals("продолжать-add-question");
    }


    private void sendRoomCreatedMessage(Update update) {
        String text = "Можете продолжать редактировать или добавлять вопросы";
        SendMessage msg = sendBotMessage.createMessage(update, text);
        sendBotMessage.sendMessage(msg);
    }
}
