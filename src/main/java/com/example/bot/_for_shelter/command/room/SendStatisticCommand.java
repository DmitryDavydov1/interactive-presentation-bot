package com.example.bot._for_shelter.command.room;

import com.example.bot._for_shelter.CustomWordCloud;
import com.example.bot._for_shelter.command.Command;
import com.example.bot._for_shelter.command.SendBotMessage;
import com.example.bot._for_shelter.models.Question;
import com.example.bot._for_shelter.models.Room;
import com.example.bot._for_shelter.models.User;
import com.example.bot._for_shelter.repository.UserRepository;
import com.example.bot._for_shelter.service.HelpService;
import com.example.bot._for_shelter.service.TelegramBot;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Component
public class SendStatisticCommand implements Command {
    private final HelpService helpService;
    private final CustomWordCloud customWordCloud;
    private final TelegramBot telegramBot;
    private static final Logger logger = LoggerFactory.getLogger(SendStatisticCommand.class);
    private final SendBotMessage sendBotMessage;


    @Lazy
    public SendStatisticCommand(HelpService helpService, CustomWordCloud customWordCloud, TelegramBot telegramBot, SendBotMessage sendBotMessage) {
        this.helpService = helpService;
        this.customWordCloud = customWordCloud;
        this.telegramBot = telegramBot;
        this.sendBotMessage = sendBotMessage;
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
            if (statistic.isEmpty()) {
                SendMessage sendMessage = sendBotMessage.createMessage(update, "На вопрос номер " + (questions.indexOf(question)+1) + " ноль ответов");
                sendBotMessage.sendMessage(sendMessage);
                logger.warn("На вопрос ноль ответов, комната {}", room.getId());
                return;
            }

            ByteArrayOutputStream fileWithCloudWord;
            try {
                fileWithCloudWord = customWordCloud.generateAndSendWordCloud(statistic);
            } catch (Exception e) {
                logger.error("Ошибка при генерации изображения", e);
                throw new RuntimeException(e);
            }


            InputFile inputFile = new InputFile(new ByteArrayInputStream(fileWithCloudWord.toByteArray()), "cloud.png");
            //Отправляем статистику каждому гостю комнаты
            for (User user : users) {
                if (!user.getChatId().equals(chatId)) {
                    telegramBot.sendPhoto(inputFile, user.getChatId());
                }
            }

            //Отправляем статистику создателю комнаты
            telegramBot.sendPhoto(inputFile, chatId);
        }

        logger.info("Успешно сгенерированы все изображения для комнаты: {}", room.getId());
    }

    @Override
    public boolean isSupport(String update) {
        return "Отправить статистику".equals(update);
    }

}
