package com.example.bot._for_shelter.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(indexes = {
        @Index(name = "idx_room_creator", columnList = "creatorRoom_id"),
        @Index(name = "idx_room_idForEntry", columnList = "idForEntry", unique = true)
})
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
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
