package com.trade.bot.mapper;

import com.trade.bot.dto.BarDto;
import com.trade.bot.dto.TickDto;
import com.trade.bot.entity.Bar;
import com.trade.bot.entity.Tick;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper
public interface BarMapper {

    List<BarDto> toDto(List<Bar> tradeEntity);

    @Mapping(target = "createDate", source = "createDate", dateFormat = "yyyy-MM-dd'T'HH:mm:ss")
    BarDto toDto(Bar tradeEntity);

}
