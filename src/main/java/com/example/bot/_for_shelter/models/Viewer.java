package com.example.bot._for_shelter.models;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(indexes = {
        @Index(name = "idx_viewer_chatId", columnList = "chatId", unique = true)
})
public class Viewer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    private String chatId;
    @OneToMany(mappedBy = "viewer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Answer> answers;


}
