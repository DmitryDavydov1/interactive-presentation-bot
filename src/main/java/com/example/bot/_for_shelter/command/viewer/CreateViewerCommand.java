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

        // Проверка состояния и отправка сообщения, если необходимо завершить ввод вопросов
        conditionRepository.findByChatId(chatId).ifPresentOrElse(condition -> {
            if ("Добавляю запросы".equals(condition.getCondition())) {
                sendMessage(update, "Сначала заверши ввод вопросов");
            } else {
                processViewer(update, chatId, userName);
            }
        }, () -> processViewer(update, chatId, userName));
    }

    @Override
    public boolean isSupport(String update) {
        return update.equals("listener-button");
    }

    private void processViewer(Update update, String chatId, String userName) {
        // Генерация клавиатуры
        InlineKeyboardMarkup markUp = markUps.menuForViewer();
        SendMessage sendMessage = sendBotMessage.createMessageWithKeyboardMarkUpWithCallbackUpdate(update, "Выберите команду", markUp);

        // Проверка существования зрителя
        if (viewerRepository.existsByChatId(chatId)) {
            sendBotMessage.sendMessage(sendMessage);
            return;
        }

        // Создание нового зрителя
        Viewer viewer = new Viewer();
        viewer.setChatId(chatId);
        viewer.setName(userName);
        viewerRepository.save(viewer);

        // Отправка сообщения
        sendBotMessage.sendMessage(sendMessage);
    }

    private void sendMessage(Update update, String message) {
        SendMessage sendMessage = sendBotMessage.createMessage(update, message);
        sendBotMessage.sendMessage(sendMessage);
    }
}
