package com.dao.bot.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Fractal {

    @Id
    private LocalDateTime createDate;
    private String symbol;
    private String high;
    private String low;
    private int interval;
    private int countBar;
}
