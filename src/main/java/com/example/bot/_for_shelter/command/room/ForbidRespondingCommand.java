package com.example.bot._for_shelter.command.room;

import com.example.bot._for_shelter.command.Command;
import com.example.bot._for_shelter.command.SendBotMessage;
import com.example.bot._for_shelter.models.Room;
import com.example.bot._for_shelter.repository.RoomRepository;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class ForbidRespondingCommand implements Command {
    private final RoomRepository roomRepository;
    private final SendBotMessage sendBotMessage;

    public ForbidRespondingCommand(RoomRepository roomRepository, SendBotMessage sendBotMessage) {
        this.roomRepository = roomRepository;
        this.sendBotMessage = sendBotMessage;
    }

    @Override
    public void execute(Update update) {
        String callbackData = update.getCallbackQuery().getData();
        long textUpdate = Long.parseLong(callbackData.split(" ")[2]);

        Room room = roomRepository.findById(textUpdate).orElse(null);
        assert room != null;
        room.setAnswerStatus(false);
        roomRepository.save(room);

        SendMessage sendMessage = sendBotMessage.createMessage(update, "Слушатели больше не могу отвечать");
        sendBotMessage.sendMessage(sendMessage);
    }

    @Override
    public boolean isSupport(String update) {
        return update.startsWith("Запрещаю отвечать");
    }
}
