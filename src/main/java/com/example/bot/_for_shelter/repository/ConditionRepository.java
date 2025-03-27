package com.example.bot._for_shelter.repository;

import org.springframework.data.jpa.repository.JpaRepository;


import com.example.bot._for_shelter.models.Condition;

import java.util.Optional;

public interface ConditionRepository extends JpaRepository<Condition, Long> {
    Optional<Condition> findByChatId(String chatId);
}
