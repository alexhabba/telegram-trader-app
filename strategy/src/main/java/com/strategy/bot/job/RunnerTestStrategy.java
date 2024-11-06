package com.strategy.bot.job;

import com.dao.bot.entity.Bar;
import com.dao.bot.entity.Deal;
import com.dao.bot.service.BarDaoService;
import com.strategy.bot.startegy.StrategyExecutor;
import com.strategy.bot.startegy.test.LimitOrderSearch;
import com.strategy.bot.startegy.test.WrapperBalance;
import com.strategy.bot.startegy.test.WrapperDouble;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Month;
import java.util.*;
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
    private final ExecutorService executorService = Executors.newFixedThreadPool(8);


    @EventListener({ContextRefreshedEvent.class})
    @SneakyThrows
    public void init() {
//        runTestStrategy();
    }

    @Scheduled(cron = "04 * * * * *")
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
        double shift = 0.001;
        while (shift < 0.1) {
            double sl = 0.8;
            while (sl < 2) {
                double tp = 1.5;
                while (tp < 5) {
                    double maxVol = 10000;
                    while (maxVol < 70000) {
                        int min = 13;
                        while (min < 100) {
                            double finalShift = shift;
                            double finalSl = sl;
                            double finalTp = tp;
                            double finalMaxVol = maxVol;
                            int finalMin = min;
                            int finalCount = count;
                            executorService.submit(() -> extracted(bars, finalShift, finalSl, finalTp, finalMaxVol, finalMin, finalCount));
                            min += 3;
                            count++;
                        }
                        maxVol += 5000;
                    }
                    tp += 0.2;
                }
                sl += 0.2;
            }
            shift += 0.003;
        }

        // 6207516
        System.out.printf("все варианты протестированы их было %d", count);
    }

    private void extracted(List<Bar> bars, double shift, double sl, double tp, double maxVol, int min, int count) {
        LinkedList<Deal> list = new LinkedList<>();
        WrapperDouble w = WrapperDouble.builder().value(0).build();
        WrapperBalance wrapperBalance = WrapperBalance.builder().balance(BigDecimal.valueOf(400)).build();
        bars.forEach(b -> limitOrderSearch.execute(b, shift, sl, tp, "7", list, maxVol, min, count, w, wrapperBalance));
        LinkedList<Deal> list1 = new LinkedList<>();
        WrapperDouble w1 = WrapperDouble.builder().value(0).build();
        WrapperBalance wrapperBalance1 = WrapperBalance.builder().balance(BigDecimal.valueOf(400)).build();
        bars.forEach(b -> limitOrderSearch.execute(b, shift, sl, tp, "8", list1, maxVol, min, count, w1, wrapperBalance1));
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

//    maxVolInStrategy = 2458,000000, min = 28, maxVol = 30000,000000, shift = 0,003000, slTemp = 1,000000, tpTemp = 1,900000, strategy = 8
//    badCount = 156, successCount : 126
//    commonResult :  1,518846

//    maxVolInStrategy = 3687,000000, min = 43, maxVol = 25000,000000, shift = 0,003000, slTemp = 1,200000, tpTemp = 3,700000, strategy = 8
//    badCount = 83, successCount : 55
//    commonResult :  1,797982

//    maxVolInStrategy = 1093,000000, min = 16, maxVol = 25000,000000, shift = 0,003000, slTemp = 1,400000, tpTemp = 4,100000, strategy = 7
//    badCount = 68, successCount : 48
//    commonResult :  1,873978

//    maxVolInStrategy = 1093,000000, min = 100, maxVol = 10000,000000, shift = 0,003000, slTemp = 1,800000, tpTemp = 1,700000, strategy = 8
//    badCount = 75, successCount : 137
//    commonResult :  1,786002

//    maxVolInStrategy = 5085,000000, min = 58, maxVol = 25000,000000, shift = 0,009000, slTemp = 1,000000, tpTemp = 5,100000, strategy = 7
//    badCount = 58, successCount : 33
//    commonResult :  2,021778

//    maxVolInStrategy = 1639,000000, min = 133, maxVol = 35000,000000, shift = 0,009000, slTemp = 0,800000, tpTemp = 3,500000, strategy = 8
//    badCount = 88, successCount : 45
//    commonResult :  1,603475

//    maxVolInStrategy = 1005,000000, min = 103, maxVol = 15000,000000, shift = 0,009000, slTemp = 2,000000, tpTemp = 2,500000, strategy = 7
//    badCount = 49, successCount : 73
//    commonResult :  1,551220

//    maxVolInStrategy = 1093,000000, min = 115, maxVol = 10000,000000, shift = 0,009000, slTemp = 2,000000, tpTemp = 2,500000, strategy = 7
//    badCount = 46, successCount : 74
//    commonResult :  1,627575

//    maxVolInStrategy = 486,000000, min = 94, maxVol = 70000,000000, shift = 0,012000, slTemp = 1,800000, tpTemp = 5,100000, strategy = 7
//    badCount = 24, successCount : 25
//    commonResult :  1,551882

//    maxVolInStrategy = 3687,000000, min = 40, maxVol = 30000,000000, shift = 0,009000, slTemp = 1,200000, tpTemp = 4,900000, strategy = 7
//    badCount = 63, successCount : 36
//    commonResult :  1,741055
}
