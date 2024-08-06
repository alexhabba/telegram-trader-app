package com.dao.bot.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Bar {

    @Id
    private LocalDateTime createDate;
    private String symbol;
    private String volBuy;
    private String volSell;
    private String open;
    private String close;
    private String low;
    private String high;
}
