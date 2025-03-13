package com.example.bot._for_shelter.service;

import com.example.bot._for_shelter.models.CreatorTheRoom;
import com.example.bot._for_shelter.models.Room;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HelpService {
    public Room findLastRoom(CreatorTheRoom creatorTheRoom) {
        List<Room> rooms = creatorTheRoom.getRoom();
        Room roomWithStatusTrue = rooms.stream()
                .filter(Room::isStatus) // Фильтруем по статусу
                .findFirst().orElse(null);
        return roomWithStatusTrue;
    }
}
