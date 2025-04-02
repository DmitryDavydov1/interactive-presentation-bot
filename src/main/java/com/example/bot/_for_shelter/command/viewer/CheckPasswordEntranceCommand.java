package com.example.bot._for_shelter.command.viewer;

import com.example.bot._for_shelter.command.Command;
import com.example.bot._for_shelter.command.SendBotMessage;
import com.example.bot._for_shelter.models.Condition;
import com.example.bot._for_shelter.models.Question;
import com.example.bot._for_shelter.models.Room;
import com.example.bot._for_shelter.models.Viewer;
import com.example.bot._for_shelter.repository.ConditionRepository;
import com.example.bot._for_shelter.repository.RoomRepository;
import com.example.bot._for_shelter.repository.ViewerRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;
import java.util.regex.Pattern;

@Component
public class CheckPasswordEntranceCommand implements Command {

    private final RoomRepository roomRepository;
    private final ConditionRepository conditionRepository;
    private final SendBotMessage sendBotMessage;
    private final ViewerRepository viewerRepository;

    public CheckPasswordEntranceCommand(RoomRepository roomRepository, ConditionRepository conditionRepository,
                                        SendBotMessage sendBotMessage, ViewerRepository viewerRepository) {
        this.roomRepository = roomRepository;
        this.conditionRepository = conditionRepository;
        this.sendBotMessage = sendBotMessage;
        this.viewerRepository = viewerRepository;
    }

    @Override
    @Transactional
    public void execute(Update update) {
        String chatId = update.getMessage().getChatId().toString();
        String password = update.getMessage().getText();

        Condition condition = getCondition(chatId);
        if (condition == null) return;

        Room room = getRoom(condition);
        if (room == null) return;

        if (!isPasswordCorrect(room, password, update)) return;

        addViewerToRoom(chatId, room);
        startQuestionnaire(update, room, condition);
    }

    private Condition getCondition(String chatId) {
        return conditionRepository.findByChatId(chatId).orElse(null);
    }

    private Room getRoom(Condition condition) {
        long roomId = Long.parseLong(condition.getCondition().split(" ")[2]);
        return roomRepository.findById(roomId).orElse(null);
    }

    private boolean isPasswordCorrect(Room room, String password, Update update) {
        if (!room.getPassword().equals(password)) {
            sendBotMessage.sendMessage(sendBotMessage.createMessage(update, "Пароль неверный. Попробуйте снова."));
            return false;
        }
        sendBotMessage.sendMessage(sendBotMessage.createMessage(update, "Пароль верный."));
        return true;
    }

    private void addViewerToRoom(String chatId, Room room) {
        Viewer viewer = viewerRepository.findByChatId(chatId);
        room.getViewers().add(viewer);
        roomRepository.save(room);
    }

    private void startQuestionnaire(Update update, Room room, Condition condition) {
        List<Question> questions = room.getQuestions();
        if (questions.isEmpty()) {
            sendBotMessage.sendMessage(sendBotMessage.createMessage(update, "В этой комнате нет вопросов."));
            return;
        }

        condition.setCondition("Отвечаю на вопрос " + room.getId() + " 0");
        conditionRepository.save(condition);

        sendBotMessage.sendMessage(sendBotMessage.createMessage(update, "Ответьте на вопрос: " + questions.get(0).getText()));
    }

    @Override
    public boolean isSupport(String update) {
        return Pattern.compile("вводит пароль (\\d+)").matcher(update).find();
    }
}
