package com.dao.bot.entity;

import com.dao.bot.enums.OrderType;
import com.dao.bot.enums.Side;
import com.dao.bot.enums.Status;
import com.dao.bot.enums.Symbol;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Необходим для заявки на покупку либо продажу
 */
@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "orderr")
public class Order {

    @Id
    private UUID orderLinkId;

    @Enumerated(EnumType.STRING)
    private Symbol symbol;

    @Enumerated(EnumType.STRING)
    private Side side;

    @Enumerated(EnumType.STRING)
    private OrderType type;

    @Enumerated(EnumType.STRING)
    private Status status;

    private String qty;

    private String price;

    @CreationTimestamp
    private LocalDateTime createDate;

    @UpdateTimestamp
    private LocalDateTime updateDate;

}
