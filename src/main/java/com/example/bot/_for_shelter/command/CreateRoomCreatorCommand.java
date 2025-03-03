package com.example.bot._for_shelter.command;

import com.example.bot._for_shelter.mark_ups.MarkUps;
import com.example.bot._for_shelter.models.CreatorTheRoom;
import com.example.bot._for_shelter.repository.CreatorTheRoomRepository;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

@Component
public class CreateRoomCreatorCommand implements Command {
    private final CreatorTheRoomRepository creatorTheRoomRepository;
    private final MarkUps markUps;
    private final SendBotMessage sendBotMessage;

    public CreateRoomCreatorCommand(CreatorTheRoomRepository creatorTheRoomRepository, MarkUps markUps, SendBotMessage sendBotMessage) {
        this.creatorTheRoomRepository = creatorTheRoomRepository;
        this.markUps = markUps;
        this.sendBotMessage = sendBotMessage;
    }

    @Override
    public void execute(Update update) {
        String chatId = String.valueOf(update.getCallbackQuery().getMessage().getChatId());

        CreatorTheRoom creatorTheRoom = new CreatorTheRoom();
        if (creatorTheRoomRepository.existsByChatId(chatId)) {
            return;
        }
        creatorTheRoom.setStatus("пока не знаю зачем");
        creatorTheRoom.setName(update.getCallbackQuery().getFrom().getUserName());
        creatorTheRoom.setChatId(chatId);

        InlineKeyboardMarkup creatorRoomMenu = markUps.creatorRoomMenu();
        SendMessage msg = new SendMessage();
        msg.setChatId(chatId);
        msg.setReplyMarkup(creatorRoomMenu);
        msg.setText("Выберите команду");

        sendBotMessage.sendMessage(msg);
        creatorTheRoomRepository.save(creatorTheRoom);
    }

    @Override
    public boolean isSupport(String update) {

        return update.equals("create-creator-command");
    }
}
