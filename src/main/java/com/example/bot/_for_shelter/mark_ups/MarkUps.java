package com.example.bot._for_shelter.mark_ups;

import com.vdurmont.emoji.EmojiParser;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Component
public class MarkUps {


    public InlineKeyboardMarkup startButton() {
        InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
        List<InlineKeyboardButton> rowInLine = new ArrayList<>();


        var spickerButton = new InlineKeyboardButton();
        String dogButtonText = EmojiParser.parseToUnicode("Я спикер" + " :dog:");


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

    public InlineKeyboardMarkup creatorRoomMenu() {
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

    public InlineKeyboardMarkup questionActivitiesButton(long questionId) {
        InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
        List<InlineKeyboardButton> rowInLine1 = new ArrayList<>();
        List<InlineKeyboardButton> rowInLine2 = new ArrayList<>();


        var editButton = new InlineKeyboardButton();
        String dogButtonText = EmojiParser.parseToUnicode("Изменить" + " ✍\uD83C\uDFFB");
        editButton.setText(dogButtonText);
        editButton.setCallbackData("edit-button-" + questionId);

        var deleteButton = new InlineKeyboardButton();
        String catButtonText = EmojiParser.parseToUnicode("Удалить вопрос " + " ❌");
        deleteButton.setText(catButtonText);
        deleteButton.setCallbackData("delete-button-" + questionId);


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

}
