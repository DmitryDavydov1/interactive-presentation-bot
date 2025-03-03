package com.example.bot._for_shelter.repository;

import com.example.bot._for_shelter.models.CreatorTheRoom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CreatorTheRoomRepository extends JpaRepository<CreatorTheRoom, Long> {
    CreatorTheRoom findByChatId(String chatId);


    boolean existsByChatId(String chatId);
}
