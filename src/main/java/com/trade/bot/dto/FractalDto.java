package com.trade.bot.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FractalDto {

    private String createDate;
    private String symbol;
    private String high;
    private String low;
    private int interval;
    private int countBar;
}
