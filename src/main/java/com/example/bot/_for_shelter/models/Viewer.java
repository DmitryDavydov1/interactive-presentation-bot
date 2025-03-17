package com.example.bot._for_shelter.models;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
public class Viewer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    private String chatId;
    @OneToMany(mappedBy = "viewer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Answer> answers;


}
