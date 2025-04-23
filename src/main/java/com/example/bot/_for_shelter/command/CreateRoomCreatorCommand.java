package com.example.bot._for_shelter.command;

import com.example.bot._for_shelter.mark_ups.MarkUps;
import com.example.bot._for_shelter.models.User;
import com.example.bot._for_shelter.repository.UserRepository;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;


@Component
public class CreateRoomCreatorCommand implements Command {
    private final MarkUps markUps;
    private final SendBotMessage sendBotMessage;
    private final UserRepository userRepository;

    public CreateRoomCreatorCommand(MarkUps markUps, SendBotMessage sendBotMessage, UserRepository userRepository) {
        this.markUps = markUps;
        this.sendBotMessage = sendBotMessage;
        this.userRepository = userRepository;
    }

    @Override
    public void execute(Update update) {
        //получаем chatId пользователя, который нажал на кнопку "Я спикер"
        String chatId = String.valueOf(update.getCallbackQuery().getMessage().getChatId());


        //Формируем сообщения для него
        InlineKeyboardMarkup creatorRoomMenu = markUps.menuForCreateRoom();
        SendMessage msg = new SendMessage();
        msg.setChatId(chatId);
        msg.setReplyMarkup(creatorRoomMenu);
        msg.setText("Выберите команду");


        //Проверяем есть ли у нас уже этот creator the room или нет
        if (userRepository.existsByChatId(chatId)) {
            sendBotMessage.sendMessage(msg);
            return;
        }

        //если нет, то создаем нового creator the room
        User creatorTheRoom = new User();


        creatorTheRoom.setChatId(chatId);

        //отправляет сообщение и сохраняет creator the room
        sendBotMessage.sendMessage(msg);
        userRepository.save(creatorTheRoom);
    }

    @Override
    public boolean isSupport(String update) {

        return update.equals("create-creator-command");
    }
}
