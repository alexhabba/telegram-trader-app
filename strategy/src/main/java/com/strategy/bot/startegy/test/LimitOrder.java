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
import com.strategy.bot.service.CommonUtils;
import com.strategy.bot.startegy.StrategyExecutor;
import com.strategy.bot.utils.PositionUtils;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.LockModeType;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Map;
import java.util.UUID;

import static com.dao.bot.enums.Status.CANCEL;
import static com.dao.bot.enums.Status.COMPLETED;
import static com.dao.bot.enums.Status.PROCESSING;
import static com.dao.bot.enums.Status.STARTED;
import static java.util.Objects.nonNull;

/**
 *
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LimitOrder implements StrategyExecutor {

    private final static Map<String, Pair<String, String>> map = Map.of(
//            // KRIS_SUB_SECOND_BYBIT 0
//            "1", Pair.of("9jaVPeAdvHrCmX0ns1", "SQnh4QIBRPY7e5ergx66hSox2LtanPfWl4J0"),
//            // KRIS_SUB_THIRD_BYBIT 106
//            "2", Pair.of("H3GirAjzpWudDl5OdM", "b0HhjkwZev5TbkeaAiyNCoPTgF03HrBfqxSS"),
//            // KRIS_SUB_FIRST_BYBIT 43
//            "3", Pair.of("AlQPnc97vD3e2rmL8g", "7nhr96hrqY1ugIVEa7Hdz4e091O63OZNvVfu"),
//            // ISLAM_BYBIT 74.51
//            "4", Pair.of("06sETlkoP2qjgAMTG5", "UN3kh8zBizlhI2U04D56nCkADUxbHsRm6g21"),
//            // ISLAM_SUB_FIRST_BYBIT 76.18
//            "5", Pair.of("GHT40gkxrAlMmYJPfk", "kORD1LFlJsS00S7mbuwSkYY8ZvN4e1s7r5Zl"),
//            // SUB_FIRST_BYBIT 100
////            "6", Pair.of("mXtga6i1kKM7E6QxZd", "xdockA1PaahdKwGecn18VgngE2ddXwhF5z0e"),
//            // islam copy
//            "6", Pair.of("Dru3SSXDYG9zyLGjKG", "R9EndOkAxzdgDZmxJyLbboNcaaGxOLxsX3xx"),
//            // ISLAM_SUB_SECOND_BYBIT 60
//            "7", Pair.of("bPVe4ZjME00iqeDAbk", "5wo5H9E2xWpxLq4t0TO6gHoSp5VhdQD7BJ88"),
            // ISLAM_SUB_THIRD_BYBIT 60
//            "8", Pair.of("mKZXsgddffQLxkBvC5", "Qlx8o0o8LgZoAI7TWIbFOzN2HPzi6faxIBxT"),
//            // islam copy
//            "8", Pair.of("Dru3SSXDYG9zyLGjKG", "R9EndOkAxzdgDZmxJyLbboNcaaGxOLxsX3xx"),
//            // SUB_THIRD_BYBIT 93.45
//            "9", Pair.of("fR9alUpUcX23hqhsBt", "Uek064v0iaYeW5HAC2oAK1QjCGihL9UwzSJ8"),
            // KRIS_BYBIT 100   запуск 20 август
            "7", Pair.of("x29QaRh6pSDzmTLUAO", "ZGDBtgo5GX1KBoLl1RTjsJk0CWHeIpwgdSxy")
            // MY MAIN ACC
//            "7", Pair.of("XoX4nqAL5ZZxqr3r0j", "TavNLVR6Q6nkbOvGye3JeeEvLNksptTwrIxF")
            // DEMO
//            "10", Pair.of("6KHHWQ26pEBvLGTvNq", "ADD12KPrgwmMBewxeWaWj1dGbLvyooJtLZYB")
//            Dru3SSXDYG9zyLGjKG
//            R9EndOkAxzdgDZmxJyLbboNcaaGxOLxsX3xx
//            2Z9USg4FZilLred2Xj
    );

//    maxVolInStrategy = 5085,000000, min = 58, maxVol = 25000,000000, shift = 0,009000, slTemp = 1,000000, tpTemp = 5,100000, strategy = 7
//    badCount = 58, successCount : 33
//    commonResult :  2,021778

    @Value("#{${accounts}}")
    private Map<Owner, Map<String, String>> keySecretMap;
    private BigDecimal resultBalance = BigDecimal.valueOf(30);

    @Value("${isTestStrategy}")
    private boolean isTestStrategy;

    @Value("${strategy}")
    private String strategy;

    @Value("${start-vol}")
    private int startVol;

    private final static double maxVol = 100_000;
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

//        positionService.setSlTp(key, secret, BigDecimal.valueOf(2.513), BigDecimal.valueOf(2));

        ResponsePosition position = positionService.getPosition(key, secret);
        System.out.println(position);
//        bybitOrderService.closeOpenLimitOrder(key, secret);

//
//        bybitOrderService.openLimitOrder(
//                key,
//                secret,
//                Symbol.WLD,
//                "2.150",
//                "2.485",
//                "5",
//                Side.Sell,
//                OrderType.LIMIT,
//                UUID.randomUUID(),
//                System.out::println);

//        BigDecimal balance = balanceService.getBalance(key, secret);
//        ResponsePosition position = positionService.getPosition(key, secret);
//        System.out.println(balance);
//        System.out.println(position);
//        System.out.println("maxVolInStrategy : " + maxVolInStrategy);

        showPositionAndBalance();

    }

    @Override
    public void execute(Bar lastBar) {
//        if (lastBar.getCreateDate().isBefore(LocalDateTime.now().minusDays(15))) {
//            return;
//        }
//        if (isTestStrategy) return;
        if (isTestStrategy && LocalDateTime.now().minusHours(4).minusMinutes(1).withSecond(0).withNano(0).equals(lastBar.getCreateDate())) {
//            deals.removeIf(d -> d.getStatus() == CANCEL || d.getStatus() == PROCESSING || d.getStatus() == STARTED);
            deals.stream().sorted(Comparator.comparing(Deal::getOpenDate))
                    .forEach(System.out::println);
            Double commonResult = deals.stream()
                    .map(deal -> deal.getResult() * deal.getVol() - deal.getVol() * 0.0015)
                    .reduce(0d, Double::sum);

            Double result = deals.stream()
                    .map(Deal::getResult)
                    .reduce(0d, Double::sum);

            long badCount = deals.stream()
                    .map(Deal::getResult)
                    .filter(r -> r < 0)
                    .count();

            long successCount = deals.stream()
                    .map(Deal::getResult)
                    .filter(r -> r > 0)
                    .count();


            System.out.println("commonResult : " + commonResult);
            System.out.println("result : " + result);
            System.out.println("убыточных сделок : " + badCount);
            System.out.println("успешных сделок : " + successCount);
            Pair<String, String> pairKeySecret = map.get(strategy);
            String key = pairKeySecret.getKey();
            String secret = pairKeySecret.getValue();
            BigDecimal balance = balanceService.getBalance(key, secret);
            ResponsePosition position = positionService.getPosition(key, secret);
            System.out.println(balance);
            System.out.println(position);
            System.out.println("maxVolInStrategy : " + maxVolInStrategy);

            System.out.println("баланс стал таким : " + resultBalance);
        }

        double volBuyLastBar = Double.parseDouble(lastBar.getVolBuy());
        double volSellLastBar = Double.parseDouble(lastBar.getVolSell());
        double closeLastBar = Double.parseDouble(lastBar.getClose());
        double openBuyLastBar = Double.parseDouble(lastBar.getOpen());

//        maxVolInStrategy = 729,000000, min = 73, maxVol = 10000,000000, shift = 0,003000, slTemp = 1,400000, tpTemp = 5,500000, strategy = 7
//        badCount = 39, successCount : 29
//        commonResult :  1,854059
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
        if (nonNull(lastDeal) && lastDeal.getStatus() == PROCESSING) {
            checkTpSl(lastBar, lastDeal);
            return;
        }

        // todo нужно реализовать механизм проверки открытия позиции через лимитку
        if (nonNull(lastDeal) && lastDeal.getStatus() == STARTED) {
            Pair<String, String> pairKeySecret = map.get(strategy);
            String key = pairKeySecret.getKey();
            String secret = pairKeySecret.getValue();
            if (isTestStrategy) {
                if (isOpenPosition(lastBar, lastDeal)) {
                    // todo если была открыта любая позиция открытая не ботом то переведет в статус PROCESSING
                    lastDeal.setStatus(PROCESSING);
                } else {
                    isCancelPosition(lastBar, lastDeal);
                }

                // если позиция есть, то открылась лимитка
            } else if (CommonUtils.isOpenPositionFromLimitOrder(key, secret, lastDeal.getId(), "WLDUSDT")) {
                // todo тут нужно доработать закрытие позиции по лимиту
                lastDeal.setStatus(PROCESSING);
                dealService.save(lastDeal);
                // todo округлить до 3 цифр или в мапу добавить
                PositionUtils.sentTpSl(key, secret, BigDecimal.valueOf(lastDeal.getSl()), BigDecimal.valueOf(lastDeal.getTp()));
                log.info("Открытие лимитной заявки, перевод в статус PROCESSING");
            } else if (isCancelPosition(lastBar, lastDeal)) {
                bybitOrderService.closeOpenLimitOrder(key, secret);
                lastDeal.setStatus(CANCEL);
                lastDeal.setCloseDate(LocalDateTime.now());
                dealService.save(lastDeal);
                log.info("Отмена лимитной заявки, перевод в статус CANCEL");
            }
            return;
        }

        if (nonNull(lastDeal) && lastDeal.getOpenDate().plusMinutes(13).isAfter(lastBar.getCreateDate())) {
            return;
        }

        double shift = 0.037;
        double openPrice = Double.parseDouble(lastBar.getClose());
        double onePercent = openPrice / 100;
        double sl = onePercent * 1.8;
        double tp = onePercent * 4.7;
        double vol = nonNull(lastDeal) && lastDeal.getResult() < 0 ? (int) Math.ceil(lastDeal.getVol() * 1.3) : startVol;

        if (isTestStrategy && vol == startVol) {
            vol = getVol(null, null);
        }

        // todo тут похоже что нужно выбрать приоритет взависимости от того какой обьем больше на покупку или продажу
        boolean isBuyMore = false;
        if (volBuyLastBar > volSellLastBar) {
            // Протестироал, с этим флагом результаты значительно улучшились
            isBuyMore = true;
        }
        if (isBuyMore && volBuyLastBar > maxVol && closeLastBar > openBuyLastBar) {
            Deal createDeal;
            if (strategy.equals("8") || strategy.equals("10")) {
                openPrice = openPrice + shift;
                createDeal = createDeal(lastBar, openPrice, Side.Sell, openPrice + sl, openPrice - tp, vol);
            } else {
                openPrice = openPrice - shift;
                createDeal = createDeal(lastBar, openPrice, Side.Buy, openPrice - sl, openPrice + tp, vol);
            }
            // открытие и сохранение сделки в БД
            if (isTestStrategy) {
                deals.add(createDeal);
            } else {
                openOrder(createDeal);
            }
        }

        if (!isBuyMore && volSellLastBar > maxVol && closeLastBar < openBuyLastBar) {
            Deal createDeal;

            if (strategy.equals("8") || strategy.equals("10")) {
                openPrice = openPrice - shift;
                createDeal = createDeal(lastBar, openPrice, Side.Buy, openPrice - sl, openPrice + tp, vol);
            } else {
                openPrice = openPrice + shift;
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

    private Deal createDeal(Bar lastBar, double openPrice, Side sell, double sl, double tp, double vol) {
//        log.info("Рабочий обьем : {}", vol);
//        log.info("Working volume : {}", vol);
        Deal createDeal = Deal.builder()
                .id(UUID.randomUUID())
                .openDate(lastBar.getCreateDate().plusMinutes(1))
                .open(openPrice)
                .status(STARTED)
                .side(sell)
                .sl(sl)
                .tp(tp)
                .vol(vol)
                .strategy(strategy)
                .build();
        if (maxVolInStrategy < vol) {
            maxVolInStrategy = vol;
        }
        return createDeal;
    }

    private void checkTpSl(Bar bar, Deal deal) {
        double low = Double.parseDouble(bar.getLow());
        double high = Double.parseDouble(bar.getHigh());

        // если позиции нет, а статус Proccesing то нужно определить закрытие позиции по sl или tp
        // если стратегия не тестовая то начинаем проверять нет ли позиции и если она есть то завершаем метод
        if (!isTestStrategy && !isNotPosition()) {
            return;
        }
        // этот сдвиг необходим только для реальной торговли так как данные разнятся между байбит и бинанс
        double shift = 0.008;
        if (isTestStrategy) {
            shift = 0;
        }
        if (deal.getSide() == Side.Buy) {
            if (low - shift <= deal.getSl()) {
                // закрытие по стоп лосс
                commonCloseAction(deal, bar, deal.getSl(), deal.getSl() - deal.getOpen());
            }

            if (high + shift >= deal.getTp()) {
                // закрытие по тейк профит
                commonCloseAction(deal, bar, deal.getTp(), deal.getTp() - deal.getOpen());
            }
        }

        if (deal.getSide() == Side.Sell) {
            if (high + shift >= deal.getSl()) {
                // закрытие по стоп лосс
                commonCloseAction(deal, bar, deal.getSl(), deal.getOpen() - deal.getSl());
            }

            if (low - shift <= deal.getTp()) {
                // закрытие по тейк профит
                commonCloseAction(deal, bar, deal.getTp(), deal.getOpen() - deal.getTp());
            }
        }

    }

    private boolean isCancelPosition(Bar bar, Deal deal) {
        LocalDateTime openDate = deal.getOpenDate()
//                .plusHours(1)
                .plusMinutes(61);
        LocalDateTime createDate = bar.getCreateDate();

        if (createDate.isAfter(openDate)) {
            deal.setStatus(CANCEL);
            deals.remove(deal);
            return true;
        }
        return false;
    }

    /**
     * Проверка открылась ли лимитная заявка или нет для теста стратегии
     *
     * @param bar
     * @param deal
     * @return
     */
    private boolean isOpenPosition(Bar bar, Deal deal) {
        double low = Double.parseDouble(bar.getLow());
        double high = Double.parseDouble(bar.getHigh());

        if (deal.getSide() == Side.Buy && low <= deal.getOpen()) {
            return true;
        } else return deal.getSide() == Side.Sell && high >= deal.getOpen();
    }

    private void commonCloseAction(Deal deal, Bar bar, double close, double result) {
        if (!isTestStrategy && !isNotPosition()) return;
        deal.setCloseDate(bar.getCreateDate().plusMinutes(1));
        deal.setStatus(COMPLETED);
        deal.setClose(close);
        deal.setResult(result);

        resultBalance = resultBalance.add(BigDecimal.valueOf(result).multiply(BigDecimal.valueOf(deal.getVol())));
        if (!isTestStrategy) {
            dealService.save(deal);
        }
    }

    private void openOrder(Deal createDeal) {
        changeDoubleValue(createDeal);
        Pair<String, String> pairKeySecret = map.get(strategy);
        String key = pairKeySecret.getKey();
        String secret = pairKeySecret.getValue();
        double size = createDeal.getVol();
        if (size == startVol) {
            size = getVol(key, secret);
            createDeal.setVol(size);
        }
        UUID orderId = null;
        if (deals.isEmpty()) {
            orderId = openOrder(
                    createDeal.getVol(),
                    createDeal.getSide(),
                    Double.toString(createDeal.getOpen()),
                    Double.toString(createDeal.getSl())
            );
        } else {
            dealService.save(createDeal);
            deals.clear();
        }
        createDeal.setId(orderId);
        try {
            dealService.save(createDeal);
            log.info("Successful save deal {}", createDeal);
        } catch (Throwable e) {
            log.error("Error save deal {}", createDeal, e);
            deals.addLast(createDeal);
        }
    }

    private void changeDoubleValue(Deal createDeal) {
        createDeal.setOpen(changeDoubleValue(createDeal.getOpen()));
        createDeal.setSl(changeDoubleValue(createDeal.getSl()));
        createDeal.setTp(changeDoubleValue(createDeal.getTp()));
    }

    private static double changeDoubleValue(double value) {
        return Math.round(value * 1000) / 1000.0;
    }

    UUID openOrder(double size, Side side, String tvh, String sl) {
        Pair<String, String> pairKeySecret = map.get(strategy);
        String key = pairKeySecret.getKey();
        String secret = pairKeySecret.getValue();

        log.info("Open limit order size : {}, side : {}, tvh : {}", size, side, tvh);
        return bybitOrderService.openLimitOrder(
                key,
                secret,
                Symbol.WLD,
                tvh,
                sl,
                Double.toString(size),
                side,
                OrderType.LIMIT,
                UUID.randomUUID(),
                d -> log.info("open order {} ", d));
    }

    @SneakyThrows
    private void showPositionAndBalance() {
        Thread.sleep(5000);
        ArrayList<BigDecimal> commonBalance = new ArrayList<>();
        map.forEach((k, v) -> {
            ResponsePosition position = positionService.getPosition(v.getKey(), v.getValue());
            BigDecimal size = position.getResult().getPositions().get(0).getSize();
            String side = position.getResult().getPositions().get(0).getSide();

            BigDecimal balance = balanceService.getBalance(v.getKey(), v.getValue());
            commonBalance.add(balance);
            System.out.println();
            System.out.println("=======================================================");
            System.out.println("account : " + k + " balance : " + balance + " side : " + side + " size : " + size);
            System.out.println("=======================================================");
        });
        System.out.println("commonBalance : " + commonBalance.stream().reduce(BigDecimal.ZERO, BigDecimal::add));
    }

    /**
     * Проверка на наличие открытых позиций
     *
     * @return TRUE - если нет открытых позиций
     */
    private boolean isNotPosition() {
        Pair<String, String> pairKeySecret = map.get(strategy);
        String key = pairKeySecret.getKey();
        String secret = pairKeySecret.getValue();

        ResponsePosition position = positionService.getPosition(key, secret);
        BigDecimal size = position.getResult().getPositions().get(0)
                .getSize();
        return size.equals(BigDecimal.ZERO);
    }

    private double getVol(String key, String secret) {

        if (!isTestStrategy) {
            resultBalance = balanceService.getBalance(key, secret);
            log.info("resultBalance = {}", resultBalance);
        }
        if (resultBalance.doubleValue() >= 5000) {
            startVol = 610;
            startVol = 987;
            startVol = 5;
        } else if (resultBalance.doubleValue() >= 2330) {
            startVol = 610;
            startVol = 144;
        } else if (resultBalance.doubleValue() >= 1440) {
            startVol = 377;
            startVol = 610;
            startVol = 89;
        } else if (resultBalance.doubleValue() >= 890) {
            startVol = 233;
            startVol = 377;
            startVol = 55;
        } else if (resultBalance.doubleValue() >= 550) {
            startVol = 34;
        } else if (resultBalance.doubleValue() >= 340) {
            startVol = 21;
        } else if (resultBalance.doubleValue() >= 210) {
            startVol = 13;
        } else if (resultBalance.doubleValue() >= 130) {
            startVol = 8;
//            startVol = 34;
        } else if (resultBalance.doubleValue() >= 80) {
            startVol = 5;
//            startVol = 21;
        } else if (resultBalance.doubleValue() >= 30) {
            startVol = 3;
            startVol = 5;
        }
        return startVol;
    }
}