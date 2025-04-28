package com.example.bot._for_shelter.config;

import com.example.bot._for_shelter.models.Room;
import lombok.Data;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;


@Configuration
@PropertySource("application.properties")
@Data
public class BotConfig {
    @Value("${bot.name}")
    String botName;

    @Value("${bot.token}")
    String token;

    public String getBotName() {
        return botName;
    }

    public String getToken() {
        return token;
    }
}
