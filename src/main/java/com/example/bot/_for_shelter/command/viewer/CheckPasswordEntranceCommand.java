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
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;
import java.util.regex.Matcher;
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
        String msg = update.getMessage().getText();
        String chatId = update.getMessage().getChatId().toString();
        Condition condition = conditionRepository.findByChatId(chatId).orElse(null);

        long idRoom = Long.parseLong(condition.getCondition().split(" ")[2]);
        Room room = roomRepository.findById(idRoom).orElse(null);

        SendMessage sendMessage;
        if (room != null) {
            if (room.getPassword().equals(msg)) {
                sendMessage = sendBotMessage.createMessage(update, "Пароль верный");
                sendBotMessage.sendMessage(sendMessage);
                Viewer viewer = viewerRepository.findByChatId(chatId);
                room.getViewers().add(viewer);
                roomRepository.save(room);


                List<Question> questionList = room.getQuestions();
                Question question = questionList.getFirst();
                condition.setCondition("Отвечаю на вопрос " + room.getId() + " " + 0);
                conditionRepository.save(condition);

                sendMessage.setText("Ответь на вопрос " + question.getText());
                sendBotMessage.sendMessage(sendMessage);

            } else {
                sendMessage = sendBotMessage.createMessage(update, "Пароль неверный");
                sendBotMessage.sendMessage(sendMessage);
            }
        }
    }

    @Override
    public boolean isSupport(String update) {
        try {
            String regex = "вводит пароль (\\d+)";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(update);

            return matcher.find();
        } catch (Exception e) {
            return false;
        }
    }
}
