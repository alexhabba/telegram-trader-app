package com.dao.bot.entity;

import com.dao.bot.enums.Side;
import com.dao.bot.enums.Symbol;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Необходим для заявки на покупку либо продажу
 */
@Data
//@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Strategy {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    private UUID id;

    @Enumerated(EnumType.STRING)
    private Symbol symbol;

    @Enumerated(EnumType.STRING)
    private Side side;

    private String qty;

    private String tvhPrice;

    private String exitPrice;

    private String profit;

    private LocalDateTime tvhDateTime;

    private LocalDateTime exitDateTime;

}
