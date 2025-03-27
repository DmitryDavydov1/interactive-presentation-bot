package com.example.bot._for_shelter.controller;

import com.example.bot._for_shelter.service.HelpService;
import com.example.bot._for_shelter.service.WebSocketService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class PictureController {

    private final WebSocketService webSocketService;

    public PictureController(WebSocketService webSocketService) {
        this.webSocketService = webSocketService;
    }

    @GetMapping("/send")
    public String sendMessage(@RequestParam HashMap<String, Integer> message) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(message);
        webSocketService.sendMessageToClients(jsonString);
        return jsonString;
    }
}
