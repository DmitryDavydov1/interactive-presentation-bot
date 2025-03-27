package com.example.bot._for_shelter.service;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class WebSocketService {
    private final SimpMessagingTemplate messagingTemplate;

    public WebSocketService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    // Отправка сообщения всем клиентам
    public void sendMessageToClients(String message) {
        messagingTemplate.convertAndSend("/topic/words", message);
    }
}
