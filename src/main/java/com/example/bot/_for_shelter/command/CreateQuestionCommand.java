package com.example.bot._for_shelter.command;

import com.example.bot._for_shelter.models.CreatorTheRoom;
import com.example.bot._for_shelter.models.Question;
import com.example.bot._for_shelter.models.Room;
import com.example.bot._for_shelter.repository.CreatorTheRoomRepository;
import com.example.bot._for_shelter.repository.QuestionRepository;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Component
public class CreateQuestionCommand implements Command {

    private final CreatorTheRoomRepository creatorTheRoomRepository;
    private final QuestionRepository questionRepository;

    public CreateQuestionCommand(CreatorTheRoomRepository creatorTheRoomRepository, QuestionRepository questionRepository) {
        this.creatorTheRoomRepository = creatorTheRoomRepository;
        this.questionRepository = questionRepository;
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
    }

    @Override
    public boolean isSupport(Update update) {
        if (update.getMessage().getText().equals("change-status")) {
            return false;
        }
        String chatId = String.valueOf(update.getMessage().getChatId());
        CreatorTheRoom creatorTheRoom = creatorTheRoomRepository.findByChatId(chatId);
        if (creatorTheRoom == null) {
            return false;
        }
        List<Room> rooms = creatorTheRoom.getRoom();

        Room roomWithStatusTrue = rooms.stream()
                .filter(Room::isStatus) // Фильтруем по статусу
                .findFirst().orElse(null);
        if (roomWithStatusTrue == null) {
            return false;
        }
        return roomWithStatusTrue.getQuestionStatus().equals("Жду вопросов");
    }
}
