package com.strategy.bot.job;

import com.dao.bot.service.BarDaoService;
import com.strategy.bot.startegy.StrategyExecutor;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RunnerTestStrategy {

    private final List<StrategyExecutor> strategyExecutorList;
    private final BarDaoService barService;

    @EventListener({ContextRefreshedEvent.class})
    @SneakyThrows
    public void init() {
        runTestStrategy();
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

        barService.findAll()
                .forEach(bar -> strategyExecutorList.forEach(strategy -> strategy.execute(bar)));
    }
}
