package com.example.bot._for_shelter.models;

import jakarta.persistence.*;
import lombok.Data;
@Entity
@Data
public class Answer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @OneToOne
    private Viewer viewer;
    @ManyToOne
    private Question question;
    private String answer;


}
