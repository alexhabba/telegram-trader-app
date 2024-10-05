package com.strategy.bot.job;

import com.dao.bot.entity.Bar;
import com.dao.bot.entity.Deal;
import com.dao.bot.service.BarDaoService;
import com.strategy.bot.startegy.StrategyExecutor;
import com.strategy.bot.startegy.test.LimitOrderSearch;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Month;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RunnerTestStrategy {

    @Value("${isTestStrategy}")
    private boolean isTestStrategy;

    private boolean isTestRun = true;

    private final List<StrategyExecutor> strategyExecutorList;
    private final BarDaoService barService;
    private final LimitOrderSearch limitOrderSearch;
    private final ExecutorService executorService = Executors.newCachedThreadPool();


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
            List<Bar> collect = barService.findAll()
                    .stream()
//                    .filter(bar -> bar.getCreateDate().getMonth() != Month.JULY)
                    .sorted(Comparator.comparing(Bar::getCreateDate))
                    .collect(Collectors.toList());
            collect.forEach(bar -> strategyExecutorList.forEach(strategy -> strategy.execute(bar)));
//            testOptimization(collect);
        } else if (!isTestStrategy) {
            barService.findLastBar(1)
                    .forEach(bar -> strategyExecutorList.forEach(strategy -> strategy.execute(bar)));
        }
    }

    public void testOptimization(List<Bar> bars) {
        int count = 0;
        double shift = 0;
        while (shift < 0.1) {
            double sl = 0.8;
            while (sl < 2) {
                double tp = 1.5;
                while (tp < 6) {
                    double maxVol = 20000;
                    while (maxVol < 100000) {
                        extracted(bars, shift, sl, tp, maxVol);
                        double finalShift = shift;
                        double finalSl = sl;
                        double finalTp = tp;
                        double finalMaxVol = maxVol;
                        executorService.submit(() -> extracted(bars, finalShift, finalSl, finalTp, finalMaxVol));
                        maxVol += 5000;
                        count++;
                    }
                    tp += 0.2;
                }
                sl += 0.2;
            }
            shift += 0.003;
        }

        System.out.printf("все варианты протестированы их было %d", count);
    }

    private void extracted(List<Bar> bars, double shift, double sl, double tp, double maxVol) {
        LinkedList<Deal> list = new LinkedList<>();
        bars.forEach(b -> limitOrderSearch.execute(b, shift, sl, tp, "7", list, maxVol));
        LinkedList<Deal> list1 = new LinkedList<>();
        bars.forEach(b -> limitOrderSearch.execute(b, shift, sl, tp, "8", list1, maxVol));
    }


//    maxVol = 45000,000000, shift = 0,003000, slTemp = 2,000000, tpTemp = 1,500000, strategy = 8
//    badCount = 62, successCount : 128
//    commonResult :  1,197940

//    maxVol = 20000,000000, shift = 0,003000, slTemp = 1,600000, tpTemp = 2,500000, strategy = 8
//    badCount = 80, successCount : 74
//    commonResult :  1,024684

//    maxVol = 30000,000000, shift = 0,006000, slTemp = 0,800000, tpTemp = 2,100000, strategy = 8
//    badCount = 149, successCount : 91
//    commonResult :  1,306017
}
