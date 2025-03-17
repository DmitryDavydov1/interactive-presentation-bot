package com.example.bot._for_shelter.repository;

import com.example.bot._for_shelter.models.Viewer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ViewerRepository extends JpaRepository<Viewer, Long> {
    boolean existsByChatId(String chatId);

    Viewer findByChatId(String chatId);
}
