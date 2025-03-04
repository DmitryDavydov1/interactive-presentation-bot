package com.example.bot._for_shelter.command.question;

import com.example.bot._for_shelter.command.Command;
import com.example.bot._for_shelter.repository.QuestionRepository;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class DeleteQuestionCommand implements Command {

    private final QuestionRepository questionRepository;

    public DeleteQuestionCommand(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    @Override
    public void execute(Update update) {
        String button = update.getCallbackQuery().getData();
        long id = Long.parseLong(button.split("-")[2]);
        questionRepository.deleteById(id);
    }

    @Override
    public boolean isSupport(String update) {
        try {
            String[] parts = update.split("-");
            return parts[0].equals("delete");
        } catch (Exception e) {
            return false;
        }
    }
}
