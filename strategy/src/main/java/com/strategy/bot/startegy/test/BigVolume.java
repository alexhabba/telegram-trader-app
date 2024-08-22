package com.strategy.bot.startegy.test;

import com.dao.bot.entity.Bar;
import com.dao.bot.entity.Deal;
import com.dao.bot.enums.OrderType;
import com.dao.bot.enums.Owner;
import com.dao.bot.enums.Side;
import com.dao.bot.enums.Symbol;
import com.dao.bot.service.BarDaoService;
import com.dao.bot.service.DealDaoService;
import com.strategy.bot.dto.ResponsePosition;
import com.strategy.bot.service.BybitBalanceService;
import com.strategy.bot.service.BybitOrderService;
import com.strategy.bot.service.BybitPositionService;
import com.strategy.bot.startegy.StrategyExecutor;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Map;
import java.util.UUID;

import static com.dao.bot.enums.Status.COMPLETED;
import static com.dao.bot.enums.Status.PROCESSING;
import static java.util.Objects.nonNull;

/**
 *
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BigVolume implements StrategyExecutor {

    private final static Map<String, Pair<String, String>> map = Map.of(
            // KRIS_SUB_SECOND_BYBIT 0
            "1", Pair.of("9jaVPeAdvHrCmX0ns1", "SQnh4QIBRPY7e5ergx66hSox2LtanPfWl4J0"),
            // KRIS_SUB_THIRD_BYBIT 106
            "2", Pair.of("H3GirAjzpWudDl5OdM", "b0HhjkwZev5TbkeaAiyNCoPTgF03HrBfqxSS"),
            // KRIS_SUB_FIRST_BYBIT 43
            "3", Pair.of("AlQPnc97vD3e2rmL8g", "7nhr96hrqY1ugIVEa7Hdz4e091O63OZNvVfu"),
            // ISLAM_BYBIT 74.51
            "4", Pair.of("06sETlkoP2qjgAMTG5", "UN3kh8zBizlhI2U04D56nCkADUxbHsRm6g21"),
            // ISLAM_SUB_FIRST_BYBIT 76.18
            "5", Pair.of("GHT40gkxrAlMmYJPfk", "kORD1LFlJsS00S7mbuwSkYY8ZvN4e1s7r5Zl"),
            // SUB_FIRST_BYBIT 100
            "6", Pair.of("UNa8RzDDztTkStiDUY", "mGwJooK5qVT4hdN3k53rGBuJDyMk8EyYoArv"),
            // ISLAM_SUB_SECOND_BYBIT 60
            "7", Pair.of("bPVe4ZjME00iqeDAbk", "5wo5H9E2xWpxLq4t0TO6gHoSp5VhdQD7BJ88"),
            // ISLAM_SUB_THIRD_BYBIT 60
            "8", Pair.of("mKZXsgddffQLxkBvC5", "Qlx8o0o8LgZoAI7TWIbFOzN2HPzi6faxIBxT"),
            // SUB_THIRD_BYBIT 93.45
            "9", Pair.of("fR9alUpUcX23hqhsBt", "Uek064v0iaYeW5HAC2oAK1QjCGihL9UwzSJ8"),
            // KRIS_BYBIT 100   запуск 20 август
            "10", Pair.of("roUwvpCiyM06jesNHS", "2xWaG3hqAddAVIJqyBozRGb3lZRjVlXmmyD3")
    );


    // KRIS_SUB_THIRD_BYBIT
    // 140      1.688
//    private final String key = "H3GirAjzpWudDl5OdM";
//    private final String secret = "b0HhjkwZev5TbkeaAiyNCoPTgF03HrBfqxSS";

    // KRIS_SUB_SECOND_BYBIT
    // 45.4  12 август
//    private final String key = "9jaVPeAdvHrCmX0ns1";
//    private final String secret = "SQnh4QIBRPY7e5ergx66hSox2LtanPfWl4J0";

    // KRIS_SUB_FIRST_BYBIT
    // 62
//    private final String key = "AlQPnc97vD3e2rmL8g";
//    private final String secret = "7nhr96hrqY1ugIVEa7Hdz4e091O63OZNvVfu";

    @Value("#{${accounts}}")
    private Map<Owner, Map<String, String>> keySecretMap;

    @Value("${isTestStrategy}")
    private boolean isTestStrategy;

    @Value("${strategy}")
    private String strategy;

    @Value("${start-vol}")
    private int startVol;

    private final static double max_vol = 50_000;
    private double maxVolInStrategy = 0;

    private final DealDaoService dealService;
    private final BarDaoService barService;
    private final BybitOrderService bybitOrderService;
    private final BybitBalanceService balanceService;
    private final BybitPositionService positionService;
    private final LinkedList<Deal> deals = new LinkedList<>();

    @EventListener({ContextRefreshedEvent.class})
    @SneakyThrows
    public void init() {
//        BigDecimal balance = balanceService.getBalance(key, secret);
//        ResponsePosition position = positionService.getPosition(key, secret);
//        System.out.println(balance);
//        System.out.println(position);
//        dealService.deleteAll();
//        openOrder("5", Side.Sell, "1.8", "1.5");
//        System.out.println();


        Pair<String, String> pairKeySecret = map.get(strategy);
        String key = pairKeySecret.getKey();
        String secret = pairKeySecret.getValue();
//        positionService.setSlTp(key, secret, BigDecimal.valueOf(1.725), BigDecimal.valueOf(1.5));

//        bybitOrderService.openOrder(
//                key,
//                secret,
//                Symbol.WLD,
//                "0",
//                "0",
//                "6000",
//                Side.Buy,
//                OrderType.MARKET,
//                UUID.randomUUID(),
//                System.out::println);

//        BigDecimal balance = balanceService.getBalance(key, secret);
//        ResponsePosition position = positionService.getPosition(key, secret);
//        System.out.println(balance);
//        System.out.println(position);
//        System.out.println("maxVolInStrategy : " + maxVolInStrategy);
    }

    @Override
    public void execute(Bar lastBar) {
//        if (isTestStrategy) return;
        if (isTestStrategy && LocalDateTime.now().minusHours(3).minusMinutes(5).withSecond(0).withNano(0).isBefore(lastBar.getCreateDate())) {
            deals.stream().sorted(Comparator.comparing(Deal::getOpenDate))
                    .forEach(System.out::println);
            Double commonResult = deals.stream()
                    .map(deal -> deal.getResult() * deal.getVol())
                    .reduce(0d, Double::sum);

            Double result = deals.stream()
                    .map(Deal::getResult)
                    .reduce(0d, Double::sum);

            System.out.println("commonResult : " + commonResult);
            System.out.println("result : " + result);
            Pair<String, String> pairKeySecret = map.get(strategy);
            String key = pairKeySecret.getKey();
            String secret = pairKeySecret.getValue();
            BigDecimal balance = balanceService.getBalance(key, secret);
            ResponsePosition position = positionService.getPosition(key, secret);
            System.out.println(balance);
            System.out.println(position);
            System.out.println("maxVolInStrategy : " + maxVolInStrategy);
        }

        double volBuyLastBar = Double.parseDouble(lastBar.getVolBuy());
        double volSellLastBar = Double.parseDouble(lastBar.getVolSell());
        double closeLastBar = Double.parseDouble(lastBar.getClose());
        double openBuyLastBar = Double.parseDouble(lastBar.getOpen());

        // Посмотреть на последнюю сделку по времени
        Deal lastDeal = null;

        if (isTestStrategy) {
            if (!deals.isEmpty()) {
                lastDeal = deals.getLast();
            }
        } else {
            lastDeal = dealService.getLastDealStrategy(strategy);
        }

        // Если есть не завершенная сделка то проверяем закрылась она или нет
        if (nonNull(lastDeal) && lastDeal.getStatus() != COMPLETED) {
            checkTpSl(lastBar, lastDeal);
            return;
        }

        if (nonNull(lastDeal) && lastDeal.getOpenDate().plusMinutes(13).isAfter(lastBar.getCreateDate())) {
            return;
        }

        double openPrice = Double.parseDouble(lastBar.getClose());
        double onePercent = openPrice / 100;
        double sl = onePercent * 1;
        double tp = onePercent * 3;
        double vol = nonNull(lastDeal) && lastDeal.getResult() < 0 ? (int) (lastDeal.getVol() * 1.4) + 13 : startVol;

        if (volBuyLastBar > max_vol && closeLastBar < openBuyLastBar) {
            Deal createDeal;
            if (strategy.equals("2") || strategy.equals("4")) {
                createDeal = createDeal(lastBar, openPrice, Side.Sell, openPrice + sl, openPrice - tp, vol);
            } else {
                createDeal = createDeal(lastBar, openPrice, Side.Buy, openPrice - sl, openPrice + tp, vol);
            }
            // открытие и сохранение сделки в БД
            if (isTestStrategy) {
                deals.add(createDeal);
            } else {
                openOrder(createDeal);
            }
        }

        if (volSellLastBar > max_vol && closeLastBar > openBuyLastBar) {
            Deal createDeal;

            if (strategy.equals("2") || strategy.equals("4")) {
                createDeal = createDeal(lastBar, openPrice, Side.Buy, openPrice - sl, openPrice + tp, vol);
            } else {
                createDeal = createDeal(lastBar, openPrice, Side.Sell, openPrice + sl, openPrice - tp, vol);
            }

            // открытие и сохранение сделки в БД
            if (isTestStrategy) {
                deals.add(createDeal);
            } else {
                openOrder(createDeal);
            }
        }

    }

    private Deal createDeal(Bar lastBar, double openPrice, Side sell, double openPrice1, double openPrice2, double vol) {
        Deal createDeal = Deal.builder()
                .id(UUID.randomUUID())
                .openDate(lastBar.getCreateDate().plusMinutes(1))
                .open(openPrice)
                .status(PROCESSING)
                .side(sell)
                .sl(openPrice1)
                .tp(openPrice2)
                .vol(vol)
                .strategy(strategy)
                .build();
        if (maxVolInStrategy < vol) {
            maxVolInStrategy = vol;
        }
        return createDeal;
    }

    private void checkTpSl(Bar bar, Deal deal) {
        if (deal.getStatus() == COMPLETED) {
            return;
        }
        double low = Double.parseDouble(bar.getLow());
        double high = Double.parseDouble(bar.getHigh());

        if (deal.getSide() == Side.Buy) {
            if (low < deal.getSl()) {
                // закрытие по стоп лосс
                commonCloseAction(deal, bar, deal.getSl(), deal.getSl() - deal.getOpen());
            }

            if (high > deal.getTp()) {
                // закрытие по тейк профит
                commonCloseAction(deal, bar, deal.getTp(), deal.getTp() - deal.getOpen());
            }
        }

        if (deal.getSide() == Side.Sell) {
            if (high > deal.getSl()) {
                // закрытие по стоп лосс
                commonCloseAction(deal, bar, deal.getSl(), deal.getOpen() - deal.getSl());
            }

            if (low < deal.getTp()) {
                // закрытие по тейк профит
                commonCloseAction(deal, bar, deal.getTp(), deal.getOpen() - deal.getTp());
            }
        }

    }

    private void commonCloseAction(Deal deal, Bar bar, double close, double result) {
        deal.setCloseDate(bar.getCreateDate().plusMinutes(1));
        deal.setStatus(COMPLETED);
        deal.setClose(close);
        deal.setResult(result);

        if (!isTestStrategy) {
            dealService.save(deal);
        }
    }

    private void openOrder(Deal createDeal) {
        if (deals.isEmpty()) {
            openOrder(
                    Double.toString(createDeal.getVol()),
                    createDeal.getSide(),
                    Double.toString(createDeal.getSl()),
                    Double.toString(createDeal.getTp())
            );
        } else {
            dealService.save(createDeal);
            deals.clear();
        }

        try {
            dealService.save(createDeal);
            log.info("Successful save deal {}", createDeal);
        } catch (Throwable e) {
            log.error("Error save deal {}", createDeal, e);
            deals.addLast(createDeal);
        }
    }

    void openOrder(String size, Side side, String sl, String tp) {
        Pair<String, String> pairKeySecret = map.get(strategy);
        String key = pairKeySecret.getKey();
        String secret = pairKeySecret.getValue();
        bybitOrderService.openOrder(
                key,
                secret,
                Symbol.WLD,
                sl,
                tp,
                size,
                side,
                OrderType.MARKET,
                UUID.randomUUID(),
                System.out::println);
    }
}
