package com.example.bot._for_shelter.models;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(indexes = {
        @Index(name = "idx_condition_chatId", columnList = "chatId", unique = true)
})
public class Condition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String chatId;
    private String condition;

}
