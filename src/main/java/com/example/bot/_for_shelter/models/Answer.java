package com.example.bot._for_shelter.models;

import jakarta.persistence.*;


@Entity
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

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @ManyToOne
    private Question question;
    private String answer;


}