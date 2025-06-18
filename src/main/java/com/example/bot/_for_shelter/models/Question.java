package com.example.bot._for_shelter.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@Getter
@Setter
@Table(indexes = {
        @Index(name = "idx_question_room", columnList = "room_id")
})
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String text;

    @ManyToOne
    private Room room;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Answer> answers;


    public Map<String, Integer> getStatistic() {
        Map<String, Integer> wordCount = new HashMap<>();

        for (Answer answer : answers) {
            // Преобразуем ответ в нижний регистр, чтобы игнорировать регистр
            String normalizedAnswer = answer.getAnswer().toLowerCase().trim(); // Удаляет пробелы в начале и конце
            wordCount.put(normalizedAnswer, wordCount.getOrDefault(normalizedAnswer, 0) + 1);
        }

        System.out.println(wordCount);
        return wordCount;
    }
}
