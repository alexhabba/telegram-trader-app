package com.trade.bot.job;

import com.trade.bot.entity.Bar;
import com.trade.bot.entity.LockJob;
import com.trade.bot.entity.Tick;
import com.trade.bot.enums.Side;
import com.trade.bot.repository.BarRepository;
import com.trade.bot.repository.LockJobRepository;
import com.trade.bot.repository.TickRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

@Slf4j
@Service
@RequiredArgsConstructor
public class BarCreator {

    private final static String JOB_NAME = "BarCreator";

    private final TickRepository tickRepository;
    private final BarRepository barRepository;
    private final LockJobRepository lockJobRepository;

    @Scheduled(fixedDelay = 1300)
    public void createBarAndSave() {
//        Optional<LockJob> lockJobOptional = lockJobRepository.findByNameAndIsLock(JOB_NAME, true);
//        if (lockJobOptional.isPresent()) {
//            return;
//        } else {
//            lockJobRepository.save(new LockJob(JOB_NAME, true));
//        }

        List<Bar> bars = barRepository.findLastBar(1);

        if (!bars.isEmpty()) {
            Bar lastBar = bars.get(0);

            LocalDateTime lastCreateDate = lastBar.getCreateDate();
            LocalDateTime start = lastCreateDate.plusMinutes(1);
            LocalDateTime end = start.plusMinutes(1);

            extracted(start, end);
        } else {
            Optional<Tick> firstTickOptional = tickRepository.findFirstTick();

            if (firstTickOptional.isPresent()) {
                Tick firstTick = firstTickOptional.get();
                LocalDateTime firstTickCreateDate = firstTick.getCreateDate();

                LocalDateTime start = firstTickCreateDate.withSecond(0).withNano(0);
                LocalDateTime end = start.plusMinutes(1);
                extracted(start, end);
            }
        }

//        lockJobRepository.save(new LockJob(JOB_NAME, false));
    }

    private void extracted(LocalDateTime start, LocalDateTime end) {
        while (end.isBefore(LocalDateTime.now().minusHours(3))) {
            Bar bar = getBar(start, end);
            if (nonNull(bar)) {
                barRepository.save(bar);
            }
            start = start.plusMinutes(1);
            end = end.plusMinutes(1);
        }
    }

    public Bar getBar(LocalDateTime start, LocalDateTime end) {
        List<Tick> tickByCreateDateBetween = tickRepository.findTickByCreateDateBetween(start, end);

        List<Tick> binance = tickByCreateDateBetween.stream()
                .filter(tick -> tick.getExchange().equals("binance"))
                .sorted(Comparator.comparing(Tick::getCreateDate))
                .collect(Collectors.toList());

        if (binance.size() == 0) return null;
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
//        try {
//
//            binance.get(binance.size() - 1).getPrice();
//        } catch (Exception e) {
//            System.out.println();
//        }
        return Bar.builder()
                .volBuy(buy.toString())
                .volSell(sell.toString())
                .close(binance.get(binance.size() - 1).getPrice())
                .open(binance.get(0).getPrice())
                .low(listLowHigh.get(0).toString())
                .high(listLowHigh.get(listLowHigh.size() - 1).toString())
                .createDate(start)
                .build();
    }
}
