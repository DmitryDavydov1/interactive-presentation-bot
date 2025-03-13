package com.example.bot._for_shelter.repository;

import com.example.bot._for_shelter.models.Question;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<Question, Long> {
}
