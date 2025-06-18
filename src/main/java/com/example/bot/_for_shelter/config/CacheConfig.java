package com.example.bot._for_shelter.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching // Включаем кеширование в Spring Boot
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager("entrance-the-room", "rooms", "questions");
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .maximumSize(300) // Максимальное число записей в кеше
                .expireAfterWrite(20, TimeUnit.MINUTES) // Время жизни записей
                .recordStats()); // Включаем статистику кеша
        return cacheManager;
    }
}
