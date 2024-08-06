package com.strategy.bot.job;

import com.dao.bot.service.BarDaoService;
import com.strategy.bot.startegy.StrategyExecutor;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RunnerTestStrategy {

    @Value("${isTestStrategy}")
    private boolean isTestStrategy;

    private boolean isTestRun = true;

    private final List<StrategyExecutor> strategyExecutorList;
    private final BarDaoService barService;

    @EventListener({ContextRefreshedEvent.class})
    @SneakyThrows
    public void init() {
//        runTestStrategy();
    }

    @Scheduled(fixedDelay = 3000)
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

        if (isTestStrategy && isTestRun) {
            isTestRun = false;
            barService.findAll()
                    .forEach(bar -> strategyExecutorList.forEach(strategy -> strategy.execute(bar)));
        } else if (!isTestStrategy) {
            barService.findLastBar(1)
                    .forEach(bar -> strategyExecutorList.forEach(strategy -> strategy.execute(bar)));
        }
    }
}
