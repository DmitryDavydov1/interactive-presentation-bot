package com.example.bot._for_shelter.models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(indexes = {
        @Index(name = "idx_answer_question", columnList = "question_id"),
        @Index(name = "idx_answer_viewer_question", columnList = "viewer_id, question_id")
})
public class Answer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @ManyToOne
    private Viewer viewer;
    @ManyToOne
    private Question question;
    private String answer;


}
