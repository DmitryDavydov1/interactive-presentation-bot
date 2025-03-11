package com.example.bot._for_shelter.command.viewer;

import com.example.bot._for_shelter.command.Command;
import com.example.bot._for_shelter.command.SendBotMessage;
import com.example.bot._for_shelter.mark_ups.MarkUps;
import com.example.bot._for_shelter.models.Condition;
import com.example.bot._for_shelter.models.Viewer;
import com.example.bot._for_shelter.repository.ConditionRepository;
import com.example.bot._for_shelter.repository.ViewerRepository;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class EntranceRoomCommand implements Command {
    private final ViewerRepository viewerRepository;
    private final SendBotMessage sendBotMessage;
    private final ConditionRepository conditionRepository;

    public EntranceRoomCommand(ViewerRepository viewerRepository, SendBotMessage sendBotMessage, ConditionRepository conditionRepository) {
        this.viewerRepository = viewerRepository;
        this.sendBotMessage = sendBotMessage;

        this.conditionRepository = conditionRepository;
    }


    @Override
    public void execute(Update update) {
        String chatId = update.getCallbackQuery().getMessage().getChatId().toString();
        Viewer viewer = viewerRepository.findByChatId(chatId);
        viewer.setStatus("Ввожу id комнаты");
        viewerRepository.save(viewer);


        Condition condition = conditionRepository.findByChatId(chatId);
        if (condition != null) {
            condition.setCondition("Ввожу id комнаты");
            conditionRepository.save(condition);
        }else {
            Condition condition1 = new Condition();
            condition1.setCondition("Ввожу id комнаты");
            condition1.setChatId(chatId);

            conditionRepository.save(condition1);
        }


        SendMessage msg = sendBotMessage.createMessage(update, "введите id комнаты");
        sendBotMessage.sendMessage(msg);
    }

    @Override
    public boolean isSupport(String update) {
        return update.equals("entrance-room");
    }
}
