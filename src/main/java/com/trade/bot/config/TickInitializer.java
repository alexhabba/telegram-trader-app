package com.trade.bot.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.trade.bot.repository.TickRepository;
import com.trade.bot.tics.MainWebSocketClient;
import com.trade.bot.tics.OkxWebSocketClientSpot;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.client.WebSocketClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.net.URISyntaxException;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class TickInitializer {

    @Autowired
    @Lazy
    private final TickRepository tickRepository;
    private final ObjectMapper objectMapper;
//    private final List<MainWebSocketClient> clients;




//    public static <T> void connect(Class<T> clazz) throws URISyntaxException {
//        if (clazz == OkxWebSocketClientSpot.class) {
////            OkxWebSocketClientSpot  client = new OkxWebSocketClientSpot(new ObjectMapper(), tickRepository);
//            try {
//                client.connectBlocking();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//    }

}
