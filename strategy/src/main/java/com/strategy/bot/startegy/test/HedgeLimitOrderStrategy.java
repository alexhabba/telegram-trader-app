package com.strategy.bot.startegy.test;

import com.bybit.api.client.domain.trade.PositionIdx;
import com.bybit.api.client.domain.trade.Side;
import com.dao.bot.entity.Account;
import com.dao.bot.entity.Bar;
import com.dao.bot.entity.Deal;
import com.dao.bot.entity.Parameter;
import com.dao.bot.enums.Owner;
import com.dao.bot.enums.Symbol;
import com.dao.bot.service.AccountService;
import com.dao.bot.service.BarService;
import com.dao.bot.service.DealService;
import com.dao.bot.service.ParameterService;
import com.strategy.bot.dto.ResponsePosition;
import com.strategy.bot.service.*;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.dao.bot.enums.Status.*;
import static com.dao.bot.enums.Symbol.SOL;
import static java.util.Objects.nonNull;

/**
 *
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class HedgeLimitOrderStrategy implements StrategyExecutor {

    private final static Map<Symbol, Double> MAP_SYMBOL_SHIFT = Map.of(
//            WLD, 0.008,
            SOL, 2.0
//            AAVE, 3.0
    );

    private final static Map<String, Pair<String, String>> map = Map.of(
            "7", Pair.of("Bm93uykPRKyNZqaGeI", "NLrdAqquHmoCjxXU3ynmx6f4XypEq5gOufMe"),
            "8", Pair.of("Bm93uykPRKyNZqaGeI", "NLrdAqquHmoCjxXU3ynmx6f4XypEq5gOufMe")
    );

    @Value("#{${accounts}}")
    private Map<Owner, Map<String, String>> keySecretMap;
    private BigDecimal resultBalance = BigDecimal.valueOf(30);

    @Value("${isTestStrategy}")
    private boolean isTestStrategy;

    private String strategy;

    @Value("${start-vol}")
    private int startVol;

    private double maxVol = 3_000;
    private long minute = 19;
    private double slParam = 1.7;
    private double tpParam = 2.5;
    private double shift = 1.3;

    private double coefficient = 1.3;
    private double volPosition = 1.3;

    private double maxVolInStrategy = 0;

    private final AccountService accountService;
    private final DealService dealService;
    private final BarService barService;
    private final OrderLimitHedgeModeService bybitOrderService;
    private final BybitBalanceService balanceService;
    private final BybitPositionService positionService;
    private final ParameterService parameterService;
    private final List<Deal> deals = new ArrayList<>();


    private List<Parameter> parameters;

    @EventListener({ContextRefreshedEvent.class})
    @SneakyThrows
    public void init() {
//        showPositionAndBalance();
//        parameters = parameterService.getParameters(SOL, List.of(7, 8));

    }

    @Override
    public void execute(Bar lastBar, LocalDateTime lastDateTime) {
//        List<Account> accounts = accountService.findAccountByIsActiveTrue();
        parameterService.getParameters(SOL, List.of(7, 8)).forEach(parameter -> {
            setParameter(parameter);
            executeRun(lastBar, lastDateTime);
        });
    }

    public void executeRun(Bar lastBar, LocalDateTime lastDateTime) {

//        if (lastBar.getCreateDate().isBefore(LocalDateTime.now().minusDays(15))) {
//            return;
//        }
//        if (isTestStrategy) return;
//        if (isTestStrategy && LocalDateTime.now().minusHours(30).minusMinutes(1).withSecond(0).withNano(0).equals(lastBar.getCreateDate())) {
        if (isTestStrategy && lastDateTime.minusMinutes(1).equals(lastBar.getCreateDate())) {
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


            System.out.println("commonResult : " + commonResult.intValue());
            System.out.println("result : " + result);
            System.out.println("убыточных сделок : " + badCount);
            System.out.println("успешных сделок : " + successCount);
            Pair<String, String> pairKeySecret = map.get(strategy);
//            String key = pairKeySecret.getKey();
//            String secret = pairKeySecret.getValue();
//            BigDecimal balance = balanceService.getBalance(key, secret);
//            ResponsePosition position = positionService.getPosition(key, secret);
//            System.out.println(balance);
//            System.out.println(position);
            System.out.println("maxVolInStrategy : " + maxVolInStrategy);

            System.out.println("баланс стал таким : " + resultBalance);
            LinkedList<Double> lst = new LinkedList<>();
            lst.addLast(125.0);
            deals.stream()
                    .map(deal -> deal.getResult() * deal.getVol())
//                    .map(Double::intValue)
                    .forEach(res -> lst.addLast(lst.getLast() + res));

            System.out.println(lst);
        }

        double volBuyLastBar = lastBar.getVolBuy();
        double volSellLastBar = lastBar.getVolSell();
        double closeLastBar = lastBar.getClose();
        double openBuyLastBar = lastBar.getOpen();

//        maxVolInStrategy = 729,000000, min = 73, maxVol = 10000,000000, shift = 0,003000, slTemp = 1,400000, tpTemp = 5,500000, strategy = 7
//        badCount = 39, successCount : 29
//        commonResult :  1,854059
        // Посмотреть на последнюю сделку по времени
        Deal lastDeal = null;

        Symbol symbol = lastBar.getSymbol();
        if (isTestStrategy) {
            if (!deals.isEmpty()) {
//                todo теперь не совсем последнюю сделку ищем
                for (int i = deals.size() - 1; i >= 0; i--) {
                    Deal deal = deals.get(i);
                    if (nonNull(deal) && deal.getStrategy().equals(strategy)) {
                        lastDeal = deal;
                        break;
                    }
                }
            }
        } else {
            lastDeal = dealService.getLastDealStrategy(strategy, symbol.name());
        }

        // Проверка(закрылась или не закрылась)
        // Если есть не завершенная сделка, то проверяем закрылась она или нет
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
                    lastDeal.setOpenDate(LocalDateTime.now());
                } else {
                    isCancelPosition(lastBar, lastDeal);
                }

                // если позиция есть, то открылась лимитка
            } else if (CommonUtils.isOpenPositionFromLimitOrder(key, secret, lastDeal.getId(), symbol + "USDT")) {
                // todo тут нужно доработать закрытие позиции по лимиту
                lastDeal.setStatus(PROCESSING);
                dealService.save(lastDeal);
                // todo округлить до 3 цифр или в мапу добавить
                PositionUtils.sentTpSl(key, secret, BigDecimal.valueOf(lastDeal.getSl()), BigDecimal.valueOf(lastDeal.getTp()), symbol);
                log.info("Открытие лимитной заявки, перевод в статус PROCESSING");
            } else if (isCancelPosition(lastBar, lastDeal)) {
                bybitOrderService.closeOpenLimitOrder(key, secret, symbol, lastDeal.getId());
                lastDeal.setStatus(CANCEL);
                lastDeal.setCloseDate(LocalDateTime.now());
                dealService.save(lastDeal);
                log.info("Отмена лимитной заявки, перевод в статус CANCEL");
            }
            return;
        }

        // не влияет на двунаправленную торговлю
        if (nonNull(lastDeal) && lastDeal.getOpenDate().plusMinutes(13).isAfter(lastBar.getCreateDate())) {
            return;
        }

//        double shift = 0.007;
        double openPrice = lastBar.getClose();
        double onePercent = openPrice / 100;
        double sl = onePercent * slParam;
        double tp = onePercent * tpParam;

//        volPosition = nonNull(lastDeal) && lastDeal.getResult() < 0 ? (int) Math.ceil(lastDeal.getVol() * 1.8) : startVol;

//        coefficient = nonNull(lastDeal) && lastDeal.getResult() < 0 ? coefficient + 0.1 : 1.3;

//        if (deals.size() > 1 && lastDeal.getResult() < 0) {
//            System.out.println();
//        }
//        coefficient = nonNull(lastDeal) && lastDeal.getResult() < 0 ? coefficient + 0.1 : 1.3;


        // 199

//        if (isTestStrategy && volPosition == startVol) {
//            volPosition = getVol(null, null);
//            // для 1000$
////            vol = 300 / Double.parseDouble(lastBar.getOpen()) * 13;
//            volPosition = 13;
//        }

        // todo тут похоже что нужно выбрать приоритет взависимости от того какой обьем больше на покупку или продажу
        boolean isBuyMore = volBuyLastBar > volSellLastBar;
        // Протестироал, с этим флагом результаты значительно улучшились
        if (isBuyMore && volBuyLastBar > maxVol && closeLastBar > openBuyLastBar) {
            Deal createDeal;
//            double avg = barService.getAvg(lastBar.getSymbol().name(), lastBar.getCreateDate());

//            vol = nonNull(lastDeal) && lastDeal.getResult() < 0 ? lastDeal.getVol() * coefficient : startVol;
//            if (isTestStrategy && vol == startVol) {
//                vol = getVol(null, null);
//            Начало 2024-07-02T20:01, конец 2025-01-09T16:09, полгода, общий результат в долларах 500, результат в пунктах 399$ убыточных сделок 282, прибыльных 123, максимальная позиция 4.6
//            }
            if (strategy.equals("8") || strategy.equals("10")
//                    && avg < Double.parseDouble(lastBar.getHigh())
            ) {
//            if (strategy.equals("8") || strategy.equals("10")) {
                openPrice = openPrice + shift;
                createDeal = createDeal(lastBar, openPrice, Side.SELL, openPrice + sl, openPrice - tp, volPosition, symbol);
            } else {
                openPrice = openPrice - shift;
                createDeal = createDeal(lastBar, openPrice, Side.BUY, openPrice - sl, openPrice + tp, volPosition, symbol);
            }
            // открытие и сохранение сделки в БД
            if (isTestStrategy) {
                deals.add(createDeal);
            } else {
                openOrder(createDeal);
            }
            return;
        }

        // todo сейчас пока счет маленький я хочу чтобы если 3 последние позиции(все одного обьема) были убыточные то увеличить обьем на 2
        getVolPosition(symbol);

        if (!isBuyMore && volSellLastBar > maxVol && closeLastBar < openBuyLastBar) {
            Deal createDeal;

//            double avg = barService.getAvg(lastBar.getSymbol().name(), lastBar.getCreateDate());
//            coefficient = nonNull(lastDeal) && lastDeal.getResult() < 0 ? coefficient + 0.1 : 1.3;
//            volPosition = nonNull(lastDeal) && lastDeal.getResult() < 0 ? lastDeal.getVol() * coefficient : volPosition;
//            if (isTestStrategy && vol == startVol) {
//                vol = getVol(null, null);
//            }
            if (strategy.equals("8") || strategy.equals("10")
//                    && avg > Double.parseDouble(lastBar.getHigh())
            ) {
                openPrice = openPrice - shift;
                createDeal = createDeal(lastBar, openPrice, Side.BUY, openPrice - sl, openPrice + tp, volPosition, symbol);
            } else {
                openPrice = openPrice + shift;
                createDeal = createDeal(lastBar, openPrice, Side.SELL, openPrice + sl, openPrice - tp, volPosition, symbol);
            }

            // открытие и сохранение сделки в БД
            if (isTestStrategy) {
                deals.add(createDeal);
            } else {
                openOrder(createDeal);
            }
        }
        return;

    }

    /**
     * Определяет позицию объема (volPosition) на основе анализа последних сделок.
     * Логика работы:
     * 1. Получает 3 последние сделки по указанному символу и стратегии
     * 2. Если все сделки имеют одинаковый объем И все сделки убыточные:
     * - Устанавливает volPosition в удвоенный объем этих сделок
     * 3. Если сделки имеют разный объем:
     * - Берет объем последней сделки, если она убыточная
     * - Иначе оставляет volPosition без изменений
     *
     * @param symbol торговый символ, для которого анализируются сделки
     */
    private void getVolPosition(Symbol symbol) {
        final int requiredDealsCount = 3;

        // Получаем 3 последние сделки
        List<Deal> recentDeals = dealService.getDealsStrategy(requiredDealsCount, strategy, symbol.name());

        // Группируем сделки по объему (vol)
        Map<Double, List<Deal>> dealsByVolume = recentDeals.stream()
                .collect(Collectors.groupingBy(Deal::getVol));

        // Если все сделки имеют одинаковый объем
        if (dealsByVolume.size() == 1) {
            // Проверяем, что все сделки убыточные
            boolean allDealsAreLoss = recentDeals.stream()
                    .allMatch(deal -> deal.getResult() < 0);

            if (allDealsAreLoss) {
                // Удваиваем объем для новой позиции
                volPosition = recentDeals.get(0).getVol() * 2;
            }
        } else {
            // Если объемы разные, работаем с последней сделкой
            Deal lastDeal = recentDeals.get(recentDeals.size() - 1);
            if (lastDeal.getResult() < 0) {
                volPosition = lastDeal.getVol();
            }
            // Если последняя сделка прибыльная - volPosition не меняется
        }
    }

    private void setParameter(Parameter parameter) {
        maxVol = parameter.getVol();
        volPosition = parameter.getVolPosition();
        minute = parameter.getMinute();
        slParam = parameter.getSl();
        tpParam = parameter.getTp();
        shift = parameter.getShift();
        coefficient = parameter.getCoefficient();
        strategy = Integer.toString(parameter.getStrategy());
    }

    private Deal createDeal(Bar lastBar, double openPrice, Side side, double sl, double tp, double vol, Symbol symbol) {
//        log.info("Рабочий обьем : {}", vol);
//        log.info("Working volume : {}", vol);
        Deal createDeal = Deal.builder()
                .id(UUID.randomUUID())
                .openDate(lastBar.getCreateDate().plusMinutes(1))
                .open(openPrice)
                .status(STARTED)
                .side(side)
                .sl(sl)
                .tp(tp)
                .vol(vol)
                .symbol(symbol)
                .strategy(strategy)
                .build();
        if (maxVolInStrategy < vol) {
            maxVolInStrategy = vol;
        }
        return createDeal;
    }

    private void checkTpSl(Bar bar, Deal deal) {
        double low = bar.getLow();
        double high = bar.getHigh();

        // если позиции нет, а статус Proccesing то нужно определить закрытие позиции по sl или tp
        // если стратегия не тестовая то начинаем проверять нет ли позиции и если она есть то завершаем метод
        if (!isTestStrategy && !isNotPosition(deal)) {
            return;
        }
        // этот сдвиг необходим только для реальной торговли так как данные разнятся между байбит и бинанс
        double shift = MAP_SYMBOL_SHIFT.get(deal.getSymbol());
        if (isTestStrategy) {
            shift = 0;
        }
        if (deal.getSide() == Side.BUY) {
            if (low - shift <= deal.getSl()) {
                // закрытие по стоп лосс
                commonCloseAction(deal, bar, deal.getSl(), deal.getSl() - deal.getOpen());
            }

            if (high + shift >= deal.getTp()) {
                // закрытие по тейк профит
                commonCloseAction(deal, bar, deal.getTp(), deal.getTp() - deal.getOpen());
            }
        }

        if (deal.getSide() == Side.SELL) {
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
                .plusMinutes(minute);
        LocalDateTime createDate = bar.getCreateDate();

        if (createDate.isAfter(openDate)) {
            deal.setStatus(CANCEL);
            deals.remove(deal);
//            coefficient = deals.size() > 1 && deals.getLast().getResult() < 0 ? coefficient - 0.1 : coefficient;

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
        double low = bar.getLow();
        double high = bar.getHigh();

        if (deal.getSide() == Side.BUY && low <= deal.getOpen()) {
            return true;
        } else return deal.getSide() == Side.SELL && high >= deal.getOpen();
    }

    private void commonCloseAction(Deal deal, Bar bar, double close, double result) {
        if (!isTestStrategy && !isNotPosition(deal)) return;
        deal.setStatus(COMPLETED);
        deal.setClose(close);
        // todo не учитывается комиссия и объем
        deal.setResult(result);

        resultBalance = resultBalance.add(BigDecimal.valueOf(result).multiply(BigDecimal.valueOf(deal.getVol())));
        if (!isTestStrategy) {
            dealService.save(deal);
        }
    }

    private void openOrder(Deal deal) {
        changeDoubleValue(deal);
        Pair<String, String> pairKeySecret = map.get(strategy);
        String key = pairKeySecret.getKey();
        String secret = pairKeySecret.getValue();
        double size = deal.getVol();
        if (size == startVol) {
            size = getVol(key, secret);
            deal.setVol(size);
        }
        UUID orderId = null;
        if (deals.isEmpty()) {
            orderId = openOrder(
                    deal.getVol(),
                    deal.getSide(),
                    Double.toString(deal.getOpen()),
                    Double.toString(deal.getSl()),
                    Double.toString(deal.getTp()),
                    deal.getSymbol(),
                    deal.getSide() == Side.SELL ? PositionIdx.HEDGE_MODE_SELL : PositionIdx.HEDGE_MODE_BUY
            );
        } else {
            dealService.save(deal);
            deals.clear();
        }
        deal.setId(orderId);
        try {
            dealService.save(deal);
            log.info("Successful save deal {}", deal);
        } catch (Throwable e) {
            log.error("Error save deal {}", deal, e);
            deals.add(deal);
        }
    }

    private void changeDoubleValue(Deal deal) {
        deal.setOpen(changeDoubleValue(deal.getOpen(), 100.0));
        deal.setSl(changeDoubleValue(deal.getSl(), 100.0));
        deal.setTp(changeDoubleValue(deal.getTp(), 100.0));
        deal.setVol(changeDoubleValue(deal.getVol(), 10.0));
    }

    private static double changeDoubleValue(double value, double round) {
        return Math.round(value * ((int) round)) / round;
    }

    UUID openOrder(double size, Side side, String tvh, String sl, String tp, Symbol symbol, PositionIdx hedgeMode) {
        Pair<String, String> pairKeySecret = map.get(strategy);
        String key = pairKeySecret.getKey();
        String secret = pairKeySecret.getValue();

        log.info("Open limit order size : {}, side : {}, tvh : {}", size, side, tvh);
        return bybitOrderService.openOrder(
                key,
                secret,
                symbol,
                tvh,
                sl,
                tp,
                Double.toString(size),
                side,
                hedgeMode
        );
    }

    @SneakyThrows
    private void showPositionAndBalance() {
        Thread.sleep(5000);
        ArrayList<BigDecimal> commonBalance = new ArrayList<>();
        map.forEach((k, v) -> {
            ResponsePosition position = positionService.getPosition(v.getKey(), v.getValue(), Symbol.SOL);
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
    private boolean isNotPosition(Deal deal) {
        Pair<String, String> pairKeySecret = map.get(strategy);
        String key = pairKeySecret.getKey();
        String secret = pairKeySecret.getValue();

        boolean isOpen = CommonUtils.isOpenPosition(key, secret, deal);

        return !isOpen;
    }

//Position{symbol='SOLUSDT', leverage='100', autoAddMargin=0, avgPrice=146.76, liqPrice=null, riskLimitValue='50000', takeProfit=null, positionValue='14.676', isReduceOnly=false, tpslMode='Full', riskId=281, trailingStop='0', unrealisedPnl='-0.011', markPrice='146.87', adlRankIndicator=0, cumRealisedPnl='-22.32050068', positionMM='0.02964552', createdTime='1733169914762', positionIdx=2, positionIM='0.17640552', seq=210657888573, updatedTime='1751795788474', side='Sell', bustPrice='', positionBalance='0', leverageSysUpdatedTime='', curRealisedPnl='-0.014676', size=0.1, positionStatus='Normal', mmrSysUpdatedTime='', stopLoss=null, tradeMode=0, sessionAvgPrice=''}
//Position{symbol='SOLUSDT', leverage='100', autoAddMargin=0, avgPrice=146.97, liqPrice=null, riskLimitValue='50000', takeProfit=null, positionValue='14.697', isReduceOnly=false, tpslMode='Full', riskId=281, trailingStop='0', unrealisedPnl='-0.01', markPrice='146.87', adlRankIndicator=0, cumRealisedPnl='0.07098046', positionMM='0.02910006', createdTime='1733169914762', positionIdx=1, positionIM='0.02910006', seq=210657688522, updatedTime='1751795581469', side='Buy', bustPrice='', positionBalance='0', leverageSysUpdatedTime='', curRealisedPnl='-0.014697', size=0.1, positionStatus='Normal', mmrSysUpdatedTime='', stopLoss=null, tradeMode=0, sessionAvgPrice=''}

    private double getVol(String key, String secret) {

        if (!isTestStrategy) {
            resultBalance = balanceService.getBalance(key, secret);
            log.info("resultBalance = {}", resultBalance);
        }
        if (resultBalance.doubleValue() >= 50000) {
            startVol = 610;
            startVol = 987;
            startVol = 5;
        }
//        } else if (resultBalance.doubleValue() >= 2330) {
//            startVol = 610;
//            startVol = 144;
//        } else if (resultBalance.doubleValue() >= 1440) {
//            startVol = 377;
//            startVol = 610;
//            startVol = 89;
//        } else if (resultBalance.doubleValue() >= 890) {
//            startVol = 233;
//            startVol = 377;
//            startVol = 55;
//        } else if (resultBalance.doubleValue() >= 550) {
//            startVol = 34;
//        } else if (resultBalance.doubleValue() >= 340) {
//            startVol = 21;
//        } else if (resultBalance.doubleValue() >= 210) {
//            startVol = 13;
//        } else if (resultBalance.doubleValue() >= 130) {
//            startVol = 8;
//            startVol = 34;
//        } else if (resultBalance.doubleValue() >= 55) {
//            startVol = 55;
//            startVol = 21;
//        } else if (resultBalance.doubleValue() >= 10) {
////            startVol = 3;
//            startVol = 13;
//        }
        // todo нужно просчитать объем контракта в зависимости от цены например:
        //  цена 300$ -> 3 контракта
        //  цена 100$ -> 9 контракта
        //  цена 10$ -> 90 контракта

//        a / b * c

        return 0.4;
//        на 100 % -> 0.5 - 1
//        на 1000 % -> 5 - 10
//        Начало 2024-07-02T20:01, конец 2025-01-09T16:09, полгода, общий результат в долларах 500, результат в пунктах 399$ если коэффициент = 1.1 рабочий обьем 1контракт убыточных сделок 282, прибыльных 123, максимальная позиция 4.6


    }

    public static void main(String[] args) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("yes", "no");
        hashMap.put("no", "no");

        int index = "yes".hashCode() & (16 - 1);
        System.out.println();
    }
}

//import java.util.UUID;
//
///**
// * Сервис бронирования места в самолете.
// * Клиент с купленным билетом может за дополнительную плату выбрать конкретное место.
// * Базовая цена мест определяется тарифами (внешним сервисом).
// * Для клиентов с определенными тарифами (PREMIUM, ULTRA) необходимо сделать скидку при оплате.
// * При бронировании клиенту выставляется инвойс на оплату. Управление оплатой осуществляется в стороннем сервисе.
// */
//@Service
//public class SeatBookingService {
//
//    @Autowired  private SeatBookingRepository seatBookingRepository;
//    @Autowired  private TicketRepository ticketRepository;
//    @Autowired  private TariffClient tariffClient;
//    @Autowired  private CustomerClient customerClient;
//    @Autowired  private PaymentClient paymentClient;
//
//    /**
//     * Бронирование.
//     * @param seatCode код места (например 19A)
//     * @param ticketId ид билета
//     */
//    @Transactional
//    public void bookSeat(String seatCode, UUID ticketId) {
//        var ticket = ticketRepository.findById(ticketId);
//        //бронируем
//        var seatBooking = new SeatBooking(seatCode, ticket.get().getFlightId(), ticketId, BookingStatus.BOOKED);
//        seatBookingRepository.save(seatBooking);
//
//        //ищем базовый тариф для выбранного места в самолете
//        var basePrice = tariffClient.getBasePrice(ticket.get().getPlaneModel(), seatCode);
//        //ищем данные о клиенте
//        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        var userData = customerClient.getCustomer(userId);
//        System.out.println("Найден пользователь " + userData.getFio() + " номер документа " + userData.getDocument());
//        var price = basePrice;
//        if (userData.getTariff() == "PREMIUM") {
//            //скидка 50%
//            price = basePrice * 0.5d;
//        }
//        if (userData.getTariff() == "ULTRA") {
//            //скидка 20%
//            price = basePrice * 0.8d;
//        }
//        var invoice = new Invoice(price, ticketId, userId);
//        //выставляем платежку
//        paymentClient.sendInvoice(invoice);
//        System.out.println("Счет выставлен");
//    }
//
//    @Data
//    @Table("seat_booking")
//    public class SeatBooking {
//
//        @Column
//        private String seatCode;
//        @Column
//        private UUID flightId;
//        @Column
//        private UUID ticketId;
//        @Column
//        private BookingStatus status;
//    }
//
//    public enum BookingStatus {
//        BOOKED, PAID;
//    }
//}


