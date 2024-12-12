package com.trade.bot.job;

import com.dao.bot.entity.Bar;
import com.dao.bot.entity.Tick;
import com.dao.bot.enums.Side;
import com.dao.bot.enums.Symbol;
import com.dao.bot.repository.BarRepository;
import com.dao.bot.repository.TickRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

@Slf4j
@Service
@RequiredArgsConstructor
public class BarCreator {

    private final static String JOB_NAME = "BarCreator";

    @Value("${hour}")
    private int hour;
    private final TickRepository tickRepository;
    private final BarRepository barRepository;
    private final ExecutorService executorService = Executors.newFixedThreadPool(10);

    @Scheduled(cron = "02 * * * * *")
    public void createBarAndSave() {
        List<String> symbols = Arrays.stream(Symbol.values()).map(Symbol::name).collect(Collectors.toList());
        List<Bar> bars = barRepository.findLastBar(symbols);


        if (bars.size() == symbols.size()) {
            bars.forEach(lastBar -> {
                LocalDateTime lastCreateDate = lastBar.getCreateDate();
                LocalDateTime start = lastCreateDate.plusMinutes(1);
                LocalDateTime end = start.plusMinutes(1);

                extracted(lastBar.getSymbol(), start, end);
            });
        } else {
            symbols.forEach(symbol -> {
                Optional<Tick> firstTickOptional = tickRepository.findFirstTick(symbol);

                if (firstTickOptional.isPresent()) {
                    Tick firstTick = firstTickOptional.get();
                    LocalDateTime firstTickCreateDate = firstTick.getCreateDate();

                    LocalDateTime start = firstTickCreateDate.withSecond(0).withNano(0);
                    LocalDateTime end = start.plusMinutes(1);
                    extracted(symbol, start, end);
                }
            });
        }
    }

    private void extracted(String symbol, LocalDateTime start, LocalDateTime end) {
        while (end.isBefore(LocalDateTime.now().minusHours(hour))) {
            Bar bar = getBar(symbol, start, end);
            if (nonNull(bar)) {
                barRepository.save(bar);
            }
            start = start.plusMinutes(1);
            end = end.plusMinutes(1);
        }
    }

    public Bar getBar(String symbol, LocalDateTime start, LocalDateTime end) {
        List<Tick> tickByCreateDateBetween = tickRepository.findTickBySymbolAndCreateDateBetween(Symbol.valueOf(symbol), start, end);

        List<Tick> binance = tickByCreateDateBetween.stream()
                .filter(tick -> tick.getExchange().equals("binance"))
                .sorted(Comparator.comparing(Tick::getCreateDate))
                .collect(Collectors.toList());

        if (binance.isEmpty()) return null;
        BigDecimal buy = binance.stream()
                .filter(tick -> tick.getSide() == Side.Buy)
                .map(Tick::getQuantity)
                .map(BigDecimal::new)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal sell = binance.stream()
                .filter(tick -> tick.getSide() == Side.Sell)
                .map(Tick::getQuantity)
                .map(BigDecimal::new)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<BigDecimal> listLowHigh = binance.stream()
                .map(Tick::getPrice)
                .map(BigDecimal::new)
                .sorted()
                .collect(Collectors.toList());

        return Bar.builder()
                .volBuy(buy.toString())
                .volSell(sell.toString())
                .close(binance.get(binance.size() - 1).getPrice())
                .open(binance.get(0).getPrice())
                .low(listLowHigh.get(0).toString())
                .high(listLowHigh.get(listLowHigh.size() - 1).toString())
                .createDate(start)
                .symbol(symbol)
                .build();
    }
}
