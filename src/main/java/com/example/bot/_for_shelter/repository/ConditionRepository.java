package com.example.bot._for_shelter.repository;

import org.springframework.data.jpa.repository.JpaRepository;


import com.example.bot._for_shelter.models.Condition;

public interface ConditionRepository extends JpaRepository<Condition, Long> {
    Condition findByChatId(String chatId);
}
