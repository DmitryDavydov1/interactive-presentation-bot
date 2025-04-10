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


    public String getStatistic() {
        Map<String, Integer> wordCount = new HashMap<>();


        for (Answer answer : answers) {
            wordCount.put(answer.getAnswer(), wordCount.getOrDefault(answer.getAnswer(), 0) + 1);
        }

        StringBuilder answer = new StringBuilder();
        for (Map.Entry<String, Integer> entry : wordCount.entrySet()) {
            answer.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }

        return answer.toString();


    }

}
