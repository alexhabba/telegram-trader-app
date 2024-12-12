package com.dao.bot.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Statistic {

    @Id
    private UUID id;
    private double maxVolInStrategy;
    private int min;
    /**
     * Всплеск обьема
     */
    private double vol;
    private double shift;
    private double sl;
    private double tp;
    private String strategy;
    private double badCount;
    private double successCount;
    private double result;
    private double commonResult;
}
