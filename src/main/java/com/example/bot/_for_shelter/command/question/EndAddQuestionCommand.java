package com.example.bot._for_shelter.command.question;

import com.example.bot._for_shelter.command.Command;
import com.example.bot._for_shelter.command.SendBotMessage;
import com.example.bot._for_shelter.models.CreatorTheRoom;
import com.example.bot._for_shelter.models.Room;
import com.example.bot._for_shelter.repository.CreatorTheRoomRepository;
import com.example.bot._for_shelter.repository.RoomRepository;
import com.example.bot._for_shelter.service.HelpService;
import org.telegram.telegrambots.meta.api.objects.Update;

public class EndAddQuestionCommand implements Command {

    private final CreatorTheRoomRepository creatorTheRoomRepository;
    private final HelpService helpService;
    private final RoomRepository roomRepository;
    private final SendBotMessage sendBotMessage;
    public EndAddQuestionCommand(CreatorTheRoomRepository creatorTheRoomRepository, HelpService helpService, RoomRepository roomRepository, SendBotMessage sendBotMessage) {
        this.creatorTheRoomRepository = creatorTheRoomRepository;
        this.helpService = helpService;
        this.roomRepository = roomRepository;
        this.sendBotMessage = sendBotMessage;
    }

    @Override
    public void execute(Update update) {
        String chatId = String.valueOf(update.getMessage().getChatId());


        CreatorTheRoom creatorTheRoom = creatorTheRoomRepository.findByChatId(chatId);
        Room roomWithStatusTrue = helpService.findLastRoom(creatorTheRoom);

        roomWithStatusTrue.setQuestionStatus("Больше не принимаю вопросы");

        roomRepository.save(roomWithStatusTrue);


    }

    @Override
    public boolean isSupport(String update) {
        try {
            String[] parts = update.split("-");
            return parts[0].equals("end");
        } catch (Exception e) {
            return false;
        }
    }
}
