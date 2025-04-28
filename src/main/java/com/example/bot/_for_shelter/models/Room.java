package com.example.bot._for_shelter.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Entity
@Table(indexes = {
        @Index(name = "idx_room_creator", columnList = "creatorRoom_id, status"),
        @Index(name = "idx_room_idForEntry", columnList = "idForEntry", unique = true)
})
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public boolean isQuestionStatus() {
        return questionStatus;
    }

    public void setQuestionStatus(boolean questionStatus) {
        this.questionStatus = questionStatus;
    }

    public User getCreatorRoom() {
        return creatorRoom;
    }

    public void setCreatorRoom(User creatorRoom) {
        this.creatorRoom = creatorRoom;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public boolean isAnswerStatus() {
        return answerStatus;
    }

    public void setAnswerStatus(boolean answerStatus) {
        this.answerStatus = answerStatus;
    }

    public int getIdForEntry() {
        return idForEntry;
    }

    public void setIdForEntry(int idForEntry) {
        this.idForEntry = idForEntry;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    private String password;
    private int idForEntry;

    @ManyToOne
    @JsonBackReference
    private User creatorRoom;

    private boolean status;
    private boolean answerStatus;
    private boolean questionStatus;

    @ManyToMany
    private List<User> users;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Question> questions;


}
