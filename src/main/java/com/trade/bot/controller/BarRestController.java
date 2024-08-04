package com.trade.bot.controller;

import com.trade.bot.dto.BarDto;
import com.trade.bot.dto.TickDto;
import com.trade.bot.service.BarService;
import com.trade.bot.service.TradeService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.QueryParam;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.isBlank;

@RestController
@RequiredArgsConstructor
@CrossOrigin
public class BarRestController {

    @Value("${hour}")
    private int hour;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

    private final BarService barService;

//    2024-07-22T23:18 buy = 4705.9 sell = 4884.6
    @GetMapping("bars")
    public ResponseEntity<List<BarDto>> getBars() {
        return ResponseEntity.ok(barService.getBars());
    }

    @GetMapping("average")
    public ResponseEntity<List<BarDto>> getBarsAverage() {
        return ResponseEntity.ok(barService.getBarss());
    }

    @GetMapping("bars-create-date-between")
    public ResponseEntity<List<BarDto>> getTicsCreateDateBetween(
            @QueryParam(value = "start") String start,
            @QueryParam(value = "end") String end
    ) {

        LocalDateTime startDateTime;
        LocalDateTime endDateTime;
        if (isBlank(start)) {
            startDateTime = LocalDateTime.now().minusHours(hour).minusMinutes(50);
        } else {
            startDateTime = LocalDateTime.parse(start, formatter);
        }

        if (isBlank(end)) {
            endDateTime = LocalDateTime.now();
        } else {
            endDateTime = LocalDateTime.parse(end, formatter);
        }

        return ResponseEntity.ok(barService.getBarsCreateDateBetween(startDateTime, endDateTime));
    }
}
