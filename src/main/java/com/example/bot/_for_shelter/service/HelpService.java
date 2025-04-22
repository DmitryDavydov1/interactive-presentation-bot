package com.example.bot._for_shelter.service;

import com.example.bot._for_shelter.models.Question;
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

    public Room findLastRoomWithoutCashing(String chatId) {
        User user = userRepository.findByChatId(chatId).orElse(null);
        assert user != null;
        return roomRepository.findRoomsByCreatorId(user.getId());
    }


    @Cacheable(value = "rooms", key = "#chatId")
    public Room findLastRoom(String chatId) {
        User user = userRepository.findByChatId(chatId).orElse(null);
        assert user != null;
        return roomRepository.findRoomsByCreatorId(user.getId());
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


    @Cacheable(value = "entrance-the-room", key = "#id", unless = "#result == null")
    public Room findRoomByIdForEntry(long id) {
        System.out.println("Fetching room from DB: " + id);
        return roomRepository.findByIdForEntry(id).orElse(null);
    }


    private int makeRandomNumber() {
        Random random = new Random();
        int randomNumber = random.nextInt(1000) + 1;
        while (roomRepository.existsByIdForEntry(randomNumber)) {
            randomNumber = random.nextInt(1000) + 1;
        }
        return randomNumber;
    }

    @Cacheable(value = "questions", key = "#room.id")
    public List<Question> getQuestionsByRoom(Room room) {
        return room.getQuestions();
    }




}
