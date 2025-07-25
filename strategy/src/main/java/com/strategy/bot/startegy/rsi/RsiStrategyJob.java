package com.strategy.bot.startegy.rsi;

import com.dao.bot.entity.Bar;
import com.dao.bot.entity.Deal;
import com.dao.bot.enums.Symbol;
import com.dao.bot.service.BarService;
import com.strategy.bot.indicator.Rsi;
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
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RsiStrategyJob {

    @Value("${isTestStrategy}")
    private boolean isTestStrategy;

    // 1 - index, 2 - windowSize, 3 - valueRsi
    private final Map<Integer, Map<Integer, Double>> MAP_INDEX_ARRAY_WINDOW_SIZE_RSI_VALUE = new HashMap<>(150000);

    private boolean isTestRun = true;

    private final BarService barService;
    private final ExecutorService executorService;
    private final RsiStrategy rsiStrategy;
    private final RsiOptimizerStrategy rsiOptimizerStrategy;


    @EventListener({ContextRefreshedEvent.class})
    @SneakyThrows
    public void init() {
//        runTestStrategy();
    }

//    @Scheduled(cron = "03 * * * * *")
    @Scheduled(fixedDelay = 1000000000)
    public void runTestStrategy() {

        if (isTestStrategy && isTestRun) {
            isTestRun = false;
            List<Bar> collect = barService.findAllBySymbol(Symbol.SOL)
                    .stream()
//                    .filter(bar -> bar.getCreateDate().getMonth() == Month.DECEMBER)
                    .sorted(Comparator.comparing(Bar::getCreateDate))
//                    .skip(280000)
//                    .filter(bar -> bar.getCreateDate().isAfter(LocalDateTime.now().minusDays(30)))
                    .collect(Collectors.toList());
            LocalDateTime lastLocalDateTime = collect.get(collect.size() - 1).getCreateDate();
            collect.forEach(bar -> rsiStrategy.execute(bar, lastLocalDateTime));
//            fillRsiMap(collect);
//            testOptimization(collect, lastLocalDateTime);
//            System.out.println();
        }

        if (!isTestStrategy) {
            rsiStrategy.execute(barService.findLastBarBySymbol(Symbol.SOL.name()), LocalDateTime.now());
        }
    }

    private void fillRsiMap(List<Bar> bars) {
        for (int i = 13; i < bars.size() - 1; i++) {
            HashMap<Integer, Double> hashMap = new HashMap<>();
            MAP_INDEX_ARRAY_WINDOW_SIZE_RSI_VALUE.put(i, hashMap);
            for (int y = 13; y <= 25; y++) {
                List<Bar> subList = safeGetWindow(i, y, bars);
                double rsiValue = Rsi.getValue(subList, y);
                hashMap.put(y, rsiValue);
            }
        }
    }

    List<Bar> safeGetWindow(int index, int windowSize, List<Bar> source) {
        int start = Math.max(0, index - windowSize);
        return source.subList(start, index);
    }

    public void testOptimization(List<Bar> bars, LocalDateTime lastLocalDateTime) {
        int count = 0;
        int windowSizeRsi = 13;
        while (windowSizeRsi <= 30) {
            double shift = 0.3;
            while (shift < 2) {
                double sl = 1;
                while (sl < 1.5) {
                    double tp = 3;
                    while (tp < 5) {
                        double maxVol = 3000;
//                        while (maxVol < 20000) {
                            int min = 13;
                            while (min < 30) {
                                double finalShift = shift;
                                double finalSl = sl;
                                double finalTp = tp;
                                double finalMaxVol = maxVol;
                                int finalMin = min;
                                int finalCount = count;
                                int finalWindowSizeRsi = windowSizeRsi;
                                executorService.submit(() -> extracted(bars, finalShift, finalSl, finalTp, finalMaxVol, finalMin, finalCount, lastLocalDateTime, finalWindowSizeRsi));
                                min += 5;
                                count++;
                            }
//                            maxVol += 2000;
//                        }
                        tp += 0.2;
                    }
                    sl += 0.2;
                }
                shift += 0.2;
            }
            windowSizeRsi += 2;
        }

        // 6207516
        System.out.printf("все варианты протестированы их было %d", count);
        System.out.println();
    }

    private void extracted(List<Bar> bars, double shift, double sl, double tp, double maxVol, int min, int count, LocalDateTime lastLocalDateTime, int windowSizeRsi) {
        LinkedList<Deal> list = new LinkedList<>();
        WrapperDouble w = WrapperDouble.builder().value(0).build();
        WrapperBalance wrapperBalance = WrapperBalance.builder().balance(BigDecimal.valueOf(400)).build();
        for (int i = 13; i < bars.size() -1; i++) {
            Double rsiValue = MAP_INDEX_ARRAY_WINDOW_SIZE_RSI_VALUE.get(i).get(windowSizeRsi);
            rsiOptimizerStrategy.execute(bars.get(i), shift, sl, tp, "7", list, maxVol, min, count, w, wrapperBalance, lastLocalDateTime, windowSizeRsi, rsiValue);
        }

        LinkedList<Deal> list1 = new LinkedList<>();
        WrapperDouble w1 = WrapperDouble.builder().value(0).build();
        WrapperBalance wrapperBalance1 = WrapperBalance.builder().balance(BigDecimal.valueOf(400)).build();
        for (int i = 13; i < bars.size() -1; i++) {
            Double rsiValue = MAP_INDEX_ARRAY_WINDOW_SIZE_RSI_VALUE.get(i).get(windowSizeRsi);
            rsiOptimizerStrategy.execute(bars.get(i), shift, sl, tp, "8", list1, maxVol, min, count, w1, wrapperBalance1, lastLocalDateTime, windowSizeRsi, rsiValue);
        }
    }

}
