package com.example.bot._for_shelter.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String password;
    private int idForEntry;
    @ManyToOne
    @JsonBackReference
    private CreatorTheRoom creatorTheRoom;
    private boolean status;
    @ManyToMany
    private List<Viewer> viewers;
    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Question> questions;


}
