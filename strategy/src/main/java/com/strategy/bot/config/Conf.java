package com.strategy.bot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class Conf {

    @Bean
    public ExecutorService executorService() {
        return Executors.newFixedThreadPool(10);
    }
}
