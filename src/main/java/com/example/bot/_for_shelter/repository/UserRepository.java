package com.example.bot._for_shelter.repository;

import com.example.bot._for_shelter.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByChatId(String chatId);

    boolean existsByChatId(String chatId);
}
