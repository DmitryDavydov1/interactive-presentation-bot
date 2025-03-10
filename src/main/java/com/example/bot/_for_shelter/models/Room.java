package com.example.bot._for_shelter.models;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String password;
    private int idForEntry;
    @ManyToOne
    private CreatorTheRoom creatorTheRoom;
    private boolean status;
    private String questionStatus = "не жду вопросов";
    private String editQuestionStatus="Не редактирую вопросы";
    @ManyToMany
    private List<Viewer> viewers;
    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Question> questions;


}
