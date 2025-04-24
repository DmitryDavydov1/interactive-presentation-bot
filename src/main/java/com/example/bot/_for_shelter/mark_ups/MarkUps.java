package com.example.bot._for_shelter.mark_ups;

import com.vdurmont.emoji.EmojiParser;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Component
public class MarkUps {


    public InlineKeyboardMarkup startMenuButton() {
        InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
        List<InlineKeyboardButton> rowInLine = new ArrayList<>();


        var spickerButton = new InlineKeyboardButton();
        String dogButtonText = EmojiParser.parseToUnicode("Я спикер");


        spickerButton.setText(dogButtonText);
        spickerButton.setCallbackData("create-creator-command");

        var catButton = new InlineKeyboardButton();
        String catButtonText = EmojiParser.parseToUnicode("Я слушатель " + " :cat:");
        catButton.setText(catButtonText);
        catButton.setCallbackData("listener-button");

        rowInLine.add(spickerButton);
        rowInLine.add(catButton);

        rowsInLine.add(rowInLine);

        markupInLine.setKeyboard(rowsInLine);
        return markupInLine;
    }

    public InlineKeyboardMarkup menuForCreateRoom() {
        InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
        List<InlineKeyboardButton> rowInLine = new ArrayList<>();


        var createRoomButton = new InlineKeyboardButton();
        String dogButtonText = EmojiParser.parseToUnicode("Cоздать комнату");


        createRoomButton.setText(dogButtonText);
        createRoomButton.setCallbackData("create_room");


        rowInLine.add(createRoomButton);


        rowsInLine.add(rowInLine);

        markupInLine.setKeyboard(rowsInLine);
        return markupInLine;
    }

    public InlineKeyboardMarkup questionActivitiesButton(long questionId, Update update) {

        Integer messageId = update.getMessage().getMessageId();

        InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
        List<InlineKeyboardButton> rowInLine1 = new ArrayList<>();
        List<InlineKeyboardButton> rowInLine2 = new ArrayList<>();


        var editButton = new InlineKeyboardButton();
        String dogButtonText = EmojiParser.parseToUnicode("Изменить" + " ✍\uD83C\uDFFB");
        editButton.setText(dogButtonText);
        editButton.setCallbackData("edit-button-" + questionId + "-" + messageId);

        var deleteButton = new InlineKeyboardButton();
        String catButtonText = EmojiParser.parseToUnicode("Удалить вопрос " + " ❌");
        deleteButton.setText(catButtonText);
        deleteButton.setCallbackData("delete-button-" + questionId + "-" + messageId);


        var endQuestion = new InlineKeyboardButton();
        String endQuestionText = EmojiParser.parseToUnicode("Завершить добавление");
        endQuestion.setText(endQuestionText);
        endQuestion.setCallbackData("end-question" + questionId);

        rowInLine1.add(editButton);
        rowInLine1.add(deleteButton);
        rowInLine2.add(endQuestion);
        rowsInLine.add(rowInLine1);
        rowsInLine.add(rowInLine2);

        markupInLine.setKeyboard(rowsInLine);
        return markupInLine;
    }


    public InlineKeyboardMarkup menuForEntranceTheRoom() {
        InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
        List<InlineKeyboardButton> rowInLine1 = new ArrayList<>();

        var entranceRoom = new InlineKeyboardButton();
        String entranceRoomText = EmojiParser.parseToUnicode("Войти в комнату");
        entranceRoom.setText(entranceRoomText);
        entranceRoom.setCallbackData("entrance-room");


        rowInLine1.add(entranceRoom);
        rowsInLine.add(rowInLine1);
        markupInLine.setKeyboard(rowsInLine);
        return markupInLine;
    }


    public InlineKeyboardMarkup menuAfterAddQuestion(long roomId) {
        InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
        List<InlineKeyboardButton> rowInLine1 = new ArrayList<>();
        List<InlineKeyboardButton> rowInLine2 = new ArrayList<>();
        List<InlineKeyboardButton> rowInLine3 = new ArrayList<>();
        List<InlineKeyboardButton> rowInLine4 = new ArrayList<>();


        var addQuestionAfterAddQuestion = new InlineKeyboardButton();
        String addQuestionText = EmojiParser.parseToUnicode("Продолжить добавлять или редактировать вопросы");
        addQuestionAfterAddQuestion.setText(addQuestionText);
        addQuestionAfterAddQuestion.setCallbackData("продолжать-add-question");


        var forbidResponding = new InlineKeyboardButton();
        String forbidRespondingText = EmojiParser.parseToUnicode("Запретить отвечать");
        forbidResponding.setText(forbidRespondingText);
        forbidResponding.setCallbackData("Запрещаю отвечать " + roomId);

        var sendStatistics = new InlineKeyboardButton();
        String sendStatisticsText = EmojiParser.parseToUnicode("Отправить облако слов");
        sendStatistics.setText(sendStatisticsText);
        sendStatistics.setCallbackData("Отправить статистику");

        var viewStatistics = new InlineKeyboardButton();
        String viewStatisticsText = EmojiParser.parseToUnicode("Посмотреть количество отвечающих");
        viewStatistics.setText(viewStatisticsText);
        viewStatistics.setCallbackData("Посмотреть статистику");


        rowInLine1.add(addQuestionAfterAddQuestion);
        rowInLine2.add(forbidResponding);
        rowInLine3.add(sendStatistics);
        rowInLine4.add(viewStatistics);

        rowsInLine.add(rowInLine1);
        rowsInLine.add(rowInLine2);
        rowsInLine.add(rowInLine3);
        rowsInLine.add(rowInLine4);

        markupInLine.setKeyboard(rowsInLine);
        return markupInLine;
    }
}
