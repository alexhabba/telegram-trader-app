package com.trade.bot.job;

import com.trade.bot.dto.BarDto;
import com.trade.bot.entity.Bar;
import com.trade.bot.service.BarService;
import com.trade.bot.startegy.StrategyExecutor;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

@Service
@RequiredArgsConstructor
public class RunnerTestStrategy {

    private final List<StrategyExecutor> strategyExecutorList;
    private final BarService barService;

    @EventListener({ContextRefreshedEvent.class})
    @SneakyThrows
    public void init() {
//        runTestStrategy();
    }

    public void runTestStrategy() {
//        List<Bar> all = barService.findAll();
//        List<BarDto> barsCreateDateBetween = barService.getBarsCreateDateBetween(LocalDateTime.now().minusDays(1), LocalDateTime.now());
//
//        all.stream()
//                .filter(b -> Double.parseDouble(b.getVolBuy()) > 200000 || Double.parseDouble(b.getVolSell()) > 200000)
//                .forEach(System.out::println);
//
//        System.out.println();
//
//        barsCreateDateBetween.stream()
//                .filter(b -> b.getVolBuy().doubleValue() > 200000 || b.getVolSell().doubleValue() > 200000)
//                .forEach(System.out::println);
//
//        OptionalInt maxBuy = barsCreateDateBetween.stream()
//                .map(BarDto::getVolBuy)
//                .mapToInt(BigDecimal::intValue)
//                .max();
//
//        OptionalInt maxSell = barsCreateDateBetween.stream()
//                .map(BarDto::getVolSell)
//                .mapToInt(BigDecimal::intValue)
//                .max();

//        barService.findAll()
//                .forEach(bar -> strategyExecutorList.forEach(strategy -> strategy.execute(bar)));
    }
}
