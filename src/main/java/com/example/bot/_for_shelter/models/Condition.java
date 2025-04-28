package com.example.bot._for_shelter.models;

import jakarta.persistence.*;


@Entity
@Table(indexes = {
        @Index(name = "idx_condition_chatId", columnList = "chatId", unique = true)
})
public class Condition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String chatId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    private String condition;

}
