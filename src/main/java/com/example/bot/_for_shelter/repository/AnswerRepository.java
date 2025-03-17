package com.example.bot._for_shelter.repository;

import com.example.bot._for_shelter.models.Answer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AnswerRepository extends JpaRepository<Answer, Long> {
    @Query("SELECT COUNT(a) FROM Answer a WHERE a.question.id IN :questionIds AND a.viewer.id = :id")
    int numberReplies(@Param("questionIds") List<Integer> questionIds, @Param("id") Integer id);

}
