package com.example.bot._for_shelter.repository;

import com.example.bot._for_shelter.models.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, Long> {
    boolean existsByIdForEntry(int randomNumber);

    @Query("SELECT r FROM Room r WHERE r.idForEntry = :idForEntry")
    Optional<Room> findByIdForEntry(long idForEntry);

    @Query("SELECT r FROM Room r WHERE r.creatorRoom.id = :creatorRoomId AND r.status = true")
    Room findRoomsByCreatorId(@Param("creatorRoomId") Long creatorRoomId);
}
