package com.trade.bot.entity;

import com.trade.bot.enums.Side;
import com.trade.bot.enums.Status;
import com.trade.bot.enums.Stepper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Deal {

    @Id
    private LocalDateTime openDate;
    private LocalDateTime closeDate;
    private double vol;
    private double open;
    private double close;
    private double tp;
    private double sl;
    @Enumerated(EnumType.STRING)
    private Side side;
    private String symbol;
    private String strategy;
    @Enumerated(EnumType.STRING)
    private Stepper stepper;
    @Enumerated(EnumType.STRING)
    private Status status;
    private double result;

}
