package com.example.bot._for_shelter.command.room;

import com.example.bot._for_shelter.CustomWordCloud;
import com.example.bot._for_shelter.command.Command;
import com.example.bot._for_shelter.models.Question;
import com.example.bot._for_shelter.models.Room;
import com.example.bot._for_shelter.models.User;
import com.example.bot._for_shelter.repository.UserRepository;
import com.example.bot._for_shelter.service.HelpService;
import com.example.bot._for_shelter.service.TelegramBot;
import jakarta.transaction.Transactional;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Component
public class SendStatisticCommand implements Command {
    private final HelpService helpService;
    private final CustomWordCloud customWordCloud;
    private final TelegramBot telegramBot;

    @Lazy
    public SendStatisticCommand(HelpService helpService, CustomWordCloud customWordCloud, TelegramBot telegramBot, UserRepository userRepository) {
        this.helpService = helpService;
        this.customWordCloud = customWordCloud;
        this.telegramBot = telegramBot;
    }

    @Override
    @Transactional
    public void execute(Update update) {
        String chatId = String.valueOf(update.getCallbackQuery().getMessage().getChatId());

        Room room = helpService.findLastRoomWithoutCashing(chatId);
        List<Question> questions = room.getQuestions();
        List<User> users = room.getUsers();


        for (Question question : questions) {

            //Получаем статистику по вопросу
            Map<String, Integer> statistic = question.getStatistic();
            File fileWithCloudWord;
            try {
                fileWithCloudWord = customWordCloud.generateAndSendWordCloud(statistic);
            } catch (IOException | TelegramApiException e) {
                throw new RuntimeException(e);
            }


            //Отправляем статистику каждому гостю комнаты
            for (User user : users) {
                telegramBot.SendPhoto(fileWithCloudWord, user.getChatId());
            }
        }
    }

    @Override
    public boolean isSupport(String update) {
        return "Отправить статистику".equals(update);
    }

}
