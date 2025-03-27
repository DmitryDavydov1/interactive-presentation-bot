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
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

@Component
public class CreateViewerCommand implements Command {
    private final ViewerRepository viewerRepository;
    private final SendBotMessage sendBotMessage;
    private final MarkUps markUps;
    private final ConditionRepository conditionRepository;

    public CreateViewerCommand(ViewerRepository viewerRepository, SendBotMessage sendBotMessage, MarkUps markUps, ConditionRepository conditionRepository) {
        this.viewerRepository = viewerRepository;
        this.sendBotMessage = sendBotMessage;
        this.markUps = markUps;
        this.conditionRepository = conditionRepository;
    }

    @Override
    public void execute(Update update) {
        String chatId = update.getCallbackQuery().getMessage().getChatId().toString();
        String userName = update.getCallbackQuery().getFrom().getUserName();


        Condition condition = conditionRepository.findByChatId(chatId).orElse(null);
        if (condition != null) {
            if (condition.getCondition().equals("Добавляю запросы")) {
                SendMessage sendMessage = sendBotMessage.createMessage(update, "Сначала зверши ввоод вопросов");
                sendBotMessage.sendMessage(sendMessage);
                return;
            }
        }

        InlineKeyboardMarkup markUp = markUps.menuForViewer();
        SendMessage sendMessage = sendBotMessage.createMessageWithKeyboardMarkUpWithCallbackUpdate(update, "Выберите команду", markUp);

        if (viewerRepository.existsByChatId(chatId)) {
            sendBotMessage.sendMessage(sendMessage);
            return;
        }

        Viewer viewer = new Viewer();
        viewer.setChatId(chatId);
        viewer.setName(userName);
        viewerRepository.save(viewer);


        sendBotMessage.sendMessage(sendMessage);
    }

    @Override
    public boolean isSupport(String update) {
        return update.equals("listener-button");
    }
}
