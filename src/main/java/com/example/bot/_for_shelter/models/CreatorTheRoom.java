package com.example.bot._for_shelter.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@Table(indexes = {
        @Index(name = "idx_creator_chatId", columnList = "chatId", unique = true)
})
public class CreatorTheRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;
    private String chatId;
    @OneToMany(mappedBy = "creatorTheRoom", cascade = CascadeType.PERSIST, orphanRemoval = true)
    @JsonManagedReference
    private List<Room> room;


}
