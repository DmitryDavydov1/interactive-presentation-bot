package com.example.bot._for_shelter.repository;

import com.example.bot._for_shelter.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByChatId(String chatId);

    boolean existsByChatId(String chatId);


    @Query(value = "SELECT EXISTS(SELECT 1 FROM room_users ru " +
            "JOIN users u ON ru.users_id = u.id " +
            "WHERE ru.room_id = :roomId AND u.chat_id = :chatId)", nativeQuery = true)
    boolean existsUserInRoom(@Param("roomId") long roomId, @Param("chatId") String chatId);



}
