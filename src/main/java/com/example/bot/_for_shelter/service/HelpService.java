package com.example.bot._for_shelter.service;

import com.example.bot._for_shelter.models.Room;
import com.example.bot._for_shelter.models.User;
import com.example.bot._for_shelter.repository.RoomRepository;
import com.example.bot._for_shelter.repository.UserRepository;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
public class HelpService {

    private final RoomRepository roomRepository;
    private final UserRepository userRepository;

    public HelpService(RoomRepository roomRepository, UserRepository userRepository) {
        this.roomRepository = roomRepository;
        this.userRepository = userRepository;
    }


    @Cacheable(value = "rooms", key = "#chatId")
    public Room findLastRoom(String chatId) {
        User creatorTheRoom = userRepository.findByChatId(chatId).orElse(null);
        List<Room> rooms = creatorTheRoom.getRoom();


        System.out.printf("%d rooms found\n", rooms.size());
        return rooms.stream()
                .filter(Room::isStatus) // Фильтруем по статусу
                .findFirst().orElse(null);
    }


    public Room findLastRoomWithoutCashing(String chatId) {
        User creatorTheRoom = userRepository.findByChatId(chatId).orElse(null);
        assert creatorTheRoom != null;
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


    @CachePut(value = "rooms", key = "#user.chatId")
    public Room updateRoom(User user) {
        Room room = new Room();
        room.setCreatorRoom(user);
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
