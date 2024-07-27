package com.trade.bot.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.trade.bot.mapper.BarMapper;
import com.trade.bot.mapper.TickMapper;
import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ConfigObjectMapper {

    @Bean
    public ObjectMapper getObjectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }

    @Bean
    public TickMapper getTickMapper() {
        return Mappers.getMapper(TickMapper.class);
    }

    @Bean
    public BarMapper getBarMapper() {
        return Mappers.getMapper(BarMapper.class);
    }
}
