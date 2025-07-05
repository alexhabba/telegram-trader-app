package com.dao.bot.entity;

import com.dao.bot.enums.Symbol;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
//@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Candle {

//    @Id
//    @GeneratedValue(generator = "UUID")
//    @GenericGenerator(
//            name = "UUID",
//            strategy = "org.hibernate.id.UUIDGenerator"
//    )
    private UUID id;
    private LocalDateTime createDate;
    private String symbol;
    private double volBuy;
    private double volSell;
    private double vol;
    private double open;
    private double close;
    private double low;
    private double high;
    private int interval;
}
