package com.example.bot._for_shelter.models;

import jakarta.persistence.*;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@Data
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
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
