package com.dao.bot.service;

import com.dao.bot.entity.TelegramUser;
import com.dao.bot.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;

    public void registerUser(Message msg) {
            var chatId = msg.getChatId();
            var chat = msg.getChat();

            TelegramUser telegramUser = new TelegramUser();

            telegramUser.setChatId(chatId);
            telegramUser.setFirstName(chat.getFirstName());
            telegramUser.setLastName(chat.getLastName());
            telegramUser.setUserName(chat.getUserName());

            repository.save(telegramUser);
    }

    public boolean isUserExist(Message msg) {
        return repository.findById(msg.getChatId()).isPresent();
    }

}
