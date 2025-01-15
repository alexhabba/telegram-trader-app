package com.dao.bot.entity;

import com.dao.bot.enums.Symbol;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.UUID;

/**
 * Набор параметров необходимый для открытия ордера
 */
@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Parameter {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    private UUID id;

    @Enumerated(EnumType.STRING)
    private Symbol symbol;

    private double tp;

    private double sl;

    private double vol;

    private double volPosition;

    private int minute;

    private double shift;

    private double coefficient;

    private int strategy;

    private String description;

}
