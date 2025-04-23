package com.example.bot._for_shelter.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(indexes = {
        @Index(name = "idx_answer_question", columnList = "question_id"),
        @Index(name = "idx_answer_viewer_question", columnList = "user_id, question_id"),
        @Index(name = "idx_answer_user", columnList = "user_id")
})
public class Answer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @ManyToOne
    private User user;
    @ManyToOne
    private Question question;
    private String answer;


}