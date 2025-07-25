package com.dao.bot.entity;

import com.bybit.api.client.domain.trade.Side;
import com.dao.bot.enums.Status;
import com.dao.bot.enums.Stepper;
import com.dao.bot.enums.Symbol;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Deal {

    @Id
    @ToString.Exclude
    private UUID id;
    private LocalDateTime openDate;
    @ToString.Exclude
    private LocalDateTime closeDate;
    private double vol;
    @ToString.Exclude
    private double open;
    @ToString.Exclude
    private double close;
    @ToString.Exclude
    private double tp;
    @ToString.Exclude
    private double sl;
    @Enumerated(EnumType.STRING)
    private Side side;
    @Enumerated(EnumType.STRING)
    @ToString.Exclude
    private Symbol symbol;
    private String strategy;
    @Enumerated(EnumType.STRING)
    @ToString.Exclude
    private Stepper stepper;
    @Enumerated(EnumType.STRING)
    @ToString.Exclude
    private Status status;
    @ToString.Exclude
    private double result;
    private double currentResult;

    public double getResult() {
        return changeDoubleValue(result, 100.0);
    }

    public double getCurrentResult() {
        return changeDoubleValue(currentResult, 100.0);
    }

    public static double changeDoubleValue(double value, double round) {
        return Math.round(value * ((int) round)) / round;
    }
}
