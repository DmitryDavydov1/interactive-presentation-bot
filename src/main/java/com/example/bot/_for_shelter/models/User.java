package com.example.bot._for_shelter.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "users", indexes = {
        @Index(name = "idx_user_chatId", columnList = "chatId", unique = true)
})
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String chatId;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Answer> answers;

    @OneToMany(mappedBy = "creatorRoom", cascade = CascadeType.PERSIST, orphanRemoval = true)
    @JsonManagedReference
    private List<Room> room;
}
