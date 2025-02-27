package com.example.bot._for_shelter.repository;

import com.example.bot._for_shelter.models.CreatorTheRoom;
import com.example.bot._for_shelter.models.Room;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepository extends JpaRepository<Room, Long> {
    boolean existsByIdForEntry(int randomNumber);

    Room findByCreatorTheRoomAndStatus(CreatorTheRoom creatorTheRoom, boolean b);
}
