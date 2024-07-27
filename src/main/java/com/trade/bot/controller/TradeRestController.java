package com.trade.bot.controller;

import com.trade.bot.dto.BarDto;
import com.trade.bot.dto.TickDto;
import com.trade.bot.service.TradeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.QueryParam;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.isBlank;

@RestController
@RequiredArgsConstructor
@CrossOrigin
public class TradeRestController {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

    private final TradeService tradeService;

    @GetMapping("ticks-create-date-between")
    public ResponseEntity<List<TickDto>> getTicsCreateDateBetween(
            @QueryParam(value = "size") double size,
            @QueryParam(value = "exchange") String exchange,
            @QueryParam(value = "start") String start,
            @QueryParam(value = "end") String end
    ) {

        LocalDateTime startDateTime;
        LocalDateTime endDateTime;
        if (isBlank(start)) {
            startDateTime = LocalDateTime.now().minusMinutes(3);
        } else {
            startDateTime = LocalDateTime.parse(start, formatter);
        }

        if (isBlank(end)) {
            endDateTime = LocalDateTime.now();
        } else {
            endDateTime = LocalDateTime.parse(end, formatter);
        }

        return ResponseEntity.ok(tradeService.getTicksCreateDateBetween(size, exchange, startDateTime, endDateTime));
    }
}
