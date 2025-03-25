package com.example.bot._for_shelter.controller;

import com.example.bot._for_shelter.service.HelpService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.util.Map;

@Controller
@RequiredArgsConstructor
public class PictureController {

    private final HelpService helpService;

    @MessageMapping("/chat")
    @SendTo("/topic/messages")
    public String processMessageFromClient(String message) {
        return "{\"response\" : \"" + helpService.test() + "\"}";
    }
}
