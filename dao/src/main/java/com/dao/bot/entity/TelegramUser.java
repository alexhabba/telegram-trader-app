package com.dao.bot.entity;

import com.dao.bot.enums.Role;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import java.sql.Timestamp;

@Data
@Entity
public class TelegramUser {

    @Id
    private Long chatId;

    private String firstName;

    private String lastName;

    private String userName;

    @Enumerated(EnumType.STRING)
    private Role role;

    private Timestamp registeredAt;

}
