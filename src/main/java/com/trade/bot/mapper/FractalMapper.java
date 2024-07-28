package com.trade.bot.mapper;

import com.trade.bot.dto.FractalDto;
import com.trade.bot.dto.TickDto;
import com.trade.bot.entity.Fractal;
import com.trade.bot.entity.Tick;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper
public interface FractalMapper {

    List<FractalDto> toDto(List<Fractal> entity);

    @Mapping(target = "createDate", source = "createDate", dateFormat = "yyyy-MM-dd'T'HH:mm:ss")
    FractalDto toDto(Fractal entity);

}
