package com.trade.bot.mapper;

import com.trade.bot.dto.TickDto;
import com.trade.bot.entity.Tick;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper
public interface TickMapper {

    List<TickDto> toDto(List<Tick> tradeEntity);

    @Mapping(target = "createDate", source = "createDate", dateFormat = "yyyy-MM-dd'T'HH:mm:ss")
    TickDto toDto(Tick tradeEntity);

}
