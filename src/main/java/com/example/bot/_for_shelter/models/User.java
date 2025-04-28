package com.example.bot._for_shelter.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;


import java.util.List;


@Entity
@Table(name = "users", indexes = {
        @Index(name = "idx_user_chatId", columnList = "chatId", unique = true)
})
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String chatId;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<Room> getRoom() {
        return room;
    }

    public void setRoom(List<Room> room) {
        this.room = room;
    }

    public List<Answer> getAnswers() {
        return answers;
    }

    public void setAnswers(List<Answer> answers) {
        this.answers = answers;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Answer> answers;

    @OneToMany(mappedBy = "creatorRoom", cascade = CascadeType.PERSIST, orphanRemoval = true)
    @JsonManagedReference
    private List<Room> room;
}
