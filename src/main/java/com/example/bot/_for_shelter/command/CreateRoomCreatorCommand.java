package com.example.bot._for_shelter.command;

import com.example.bot._for_shelter.models.CreatorTheRoom;
import com.example.bot._for_shelter.repository.CreatorTheRoomRepository;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class CreateRoomCreatorCommand implements Command {
    private final CreatorTheRoomRepository creatorTheRoomRepository;

    public CreateRoomCreatorCommand(CreatorTheRoomRepository creatorTheRoomRepository) {
        this.creatorTheRoomRepository = creatorTheRoomRepository;
    }

    @Override
    public void execute(Update update) {
        String chatId = String.valueOf(update.getMessage().getChatId());
        CreatorTheRoom creatorTheRoom = new CreatorTheRoom();
        creatorTheRoom.setStatus("пока не знаю зачем");
        creatorTheRoom.setName(update.getMessage().getFrom().getUserName());
        creatorTheRoom.setChatId(chatId);

        creatorTheRoomRepository.save(creatorTheRoom);
    }

    @Override
    public boolean isSupport(Update update) {
        String text = update.getMessage().getText();

        return text.equals("create-creator-command");
    }
}
