package com.example.bot._for_shelter.service;

import com.example.bot._for_shelter.models.CreatorTheRoom;
import com.example.bot._for_shelter.models.Room;
import com.example.bot._for_shelter.repository.CreatorTheRoomRepository;
import com.example.bot._for_shelter.repository.RoomRepository;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
public class HelpService {

    private final RoomRepository roomRepository;
    private final CreatorTheRoomRepository creatorTheRoomRepository;

    public HelpService(RoomRepository roomRepository, CreatorTheRoomRepository creatorTheRoomRepository) {
        this.roomRepository = roomRepository;
        this.creatorTheRoomRepository = creatorTheRoomRepository;
    }

    @Cacheable(value = "rooms", key = "#chatId")
    public Room findLastRoom(String chatId) {
        CreatorTheRoom creatorTheRoom = creatorTheRoomRepository.findByChatId(chatId);
        List<Room> rooms = creatorTheRoom.getRoom();


        System.out.printf("%d rooms found\n", rooms.size());
        return rooms.stream()
                .filter(Room::isStatus) // Фильтруем по статусу
                .findFirst().orElse(null);
    }

    public Room findLastRoomWithoutCashing(String chatId) {
        CreatorTheRoom creatorTheRoom = creatorTheRoomRepository.findByChatId(chatId);
        List<Room> rooms = creatorTheRoom.getRoom();


        System.out.printf("%d rooms found\n", rooms.size());
        return rooms.stream()
                .filter(Room::isStatus) // Фильтруем по статусу
                .findFirst().orElse(null);
    }


    @Cacheable(value = "entrance-the-room", key = "#id", unless = "#result == null")
    public Room findRoomByIdForEntry(long id) {
        System.out.println("Fetching room from DB: " + id);
        return roomRepository.findByIdForEntry(id).orElse(null);
    }

    @CachePut(value = "rooms", key = "#creatorTheRoom.chatId")
    public Room updateRoom(CreatorTheRoom creatorTheRoom) {
        Room room = new Room();
        room.setCreatorTheRoom(creatorTheRoom);
        room.setStatus(true);
        room.setQuestionStatus(true);
        room.setAnswerStatus(true);

        int random = makeRandomNumber();
        room.setIdForEntry(random);
        roomRepository.save(room);
        return room;
    }

    private int makeRandomNumber() {
        Random random = new Random();
        int randomNumber = random.nextInt(1000) + 1;
        while (roomRepository.existsByIdForEntry(randomNumber)) {
            randomNumber = random.nextInt(1000) + 1;
        }
        return randomNumber;
    }

}
