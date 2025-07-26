package com.strategy.bot.startegy.rsi;

import com.bybit.api.client.domain.trade.PositionIdx;
import com.bybit.api.client.domain.trade.Side;
import com.dao.bot.entity.Account;
import com.dao.bot.entity.Bar;
import com.dao.bot.entity.Deal;
import com.dao.bot.entity.Parameter;
import com.dao.bot.enums.Symbol;
import com.dao.bot.service.AccountService;
import com.dao.bot.service.BarService;
import com.dao.bot.service.DealService;
import com.dao.bot.service.ParameterService;
import com.strategy.bot.indicator.Rsi;
import com.strategy.bot.service.BybitBalanceService;
import com.strategy.bot.service.BybitPositionService;
import com.strategy.bot.service.CommonUtils;
import com.strategy.bot.service.OrderLimitHedgeModeService;
import com.strategy.bot.startegy.StrategyExecutor;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.dao.bot.enums.Status.*;
import static com.dao.bot.enums.Symbol.SOL;
import static java.util.Objects.nonNull;

/**
 * Стратегия торговли на основе RSI с использованием лимитных ордеров в режиме хеджирования.
 * Основные особенности:
 * - Использует RSI для определения точек входа
 * - Работает с лимитными ордерами со смещением от текущей цены
 * - Поддерживает хеджирование (одновременные длинные и короткие позиции)
 * - Автоматически регулирует объем позиции на основе предыдущих результатов
 */
@Timed  // Замеряет все методы в классе
@Slf4j
@Service
@RequiredArgsConstructor
public class RsiStrategy implements StrategyExecutor {

    private final Map<LocalDateTime, Map<Integer, Double>> MAP_DATE_MAP_WINDOW_SIZE_RSI_VALUE = new HashMap<>(400000);


    public static final Map<String, Integer> MAP_STRATEGY_WINDOW_SIZE_RSI = Map.of(
            "7", 13,
            "8", 13,
            "9", 6,
            "6", 6
    );

//    private final static Map<String, Pair<String, String>> map = Map.of(
//            "7", Pair.of("Bm93uykPRKyNZqaGeI", "NLrdAqquHmoCjxXU3ynmx6f4XypEq5gOufMe"),
//            "8", Pair.of("Bm93uykPRKyNZqaGeI", "NLrdAqquHmoCjxXU3ynmx6f4XypEq5gOufMe"),
//            "10", Pair.of("pcmNk8vTZurgJQHM9b", "NrKYnnW37Xfd42vbXpOcM7VyKrCgRTbzd7k9"),
//            "1", Pair.of("pcmNk8vTZurgJQHM9b", "NrKYnnW37Xfd42vbXpOcM7VyKrCgRTbzd7k9")
//    );

    private BigDecimal resultBalance = BigDecimal.valueOf(30);

    @Value("${isTestStrategy}")
    private boolean isTestStrategy;

    private String strategy;

    private double maxVol = 3_000;
    private long minute = 19;
    private double slParam = 1.7;
    private double tpParam = 2.5;
    private double shift = 1.3;

    private double coefficient = 1.3;
    private double volPosition = 1.3;

    private double maxVolInStrategy = 0;
    private Account account;

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
        account = accountService.findAccountByIsActiveTrue("first").orElse(new Account());
    }

    boolean fl = true;

    @Override
    public void execute(Bar lastBar, LocalDateTime lastDateTime) {
        if (isTestStrategy && fl) {
            fillRsiMap(barService.findAll());
            fl = false;
        }

        List<Parameter> parameters1 = parameterService.getParameters(SOL, List.of(6, 7, 8, 9));
//        List<Parameter> parameters1 = parameterService.getParameters(SOL, List.of(8));
        parameters1.forEach(parameter -> {
            setParameter(parameter);
            executeRun(lastBar, lastDateTime);
        });
    }

    private boolean isPrintRes = true;

    public void executeRun(Bar lastBar, LocalDateTime lastDateTime) {

        if (isPrintRes && isTestStrategy && lastDateTime.minusMinutes(1).equals(lastBar.getCreateDate())) {
            isPrintRes = false;
//            dealService.saveAll(deals);
            for (int i = 0; i < deals.size() - 1; i++) {
                System.out.println(i + 1 + ".  " + deals.get(i));
            }
            Double commonResult = deals.stream()
                    .map(deal -> deal.getResult() * deal.getVol() - deal.getVol() * 0.015)
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
            System.out.println("maxVolInStrategy : " + maxVolInStrategy);

            System.out.println("баланс стал таким : " + resultBalance);
            LinkedList<Double> lst = new LinkedList<>();
            lst.addLast(125.0);
            deals.stream()
                    .map(deal -> deal.getResult() * deal.getVol())
                    .forEach(res -> lst.addLast(lst.getLast() + res));

            List<String> lstString = lst.stream()
                    .map(number -> String.format("%.1f", number).replace(",", "."))
                    .collect(Collectors.toList());

            System.out.println(lstString);
        }

        double volBuyLastBar = lastBar.getVolBuy();
        double volSellLastBar = lastBar.getVolSell();
        double closeLastBar = lastBar.getClose();
        double openBuyLastBar = lastBar.getOpen();


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
            String key = account.getKey();
            String secret = account.getSecret();
            if (isTestStrategy) {
                if (isOpenPosition(lastBar, lastDeal)) {
                    // todo если была открыта любая позиция открытая не ботом то переведет в статус PROCESSING
                    lastDeal.setStatus(PROCESSING);
                    lastDeal.setOpenDate(lastBar.getCreateDate());
                } else {
                    isCancelPosition(lastBar, lastDeal);
                }

                // если позиция есть, то открылась лимитка
            } else if (CommonUtils.isOpenPositionFromLimitOrder(key, secret, lastDeal.getId(), symbol + "USDT")) {
                // todo тут нужно доработать закрытие позиции по лимиту
                lastDeal.setStatus(PROCESSING);
                dealService.save(lastDeal);
                // todo округлить до 3 цифр или в мапу добавить
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

        double openPrice = lastBar.getClose();
        double onePercent = openPrice / 100;
        double sl = onePercent * slParam;
        double tp = onePercent * tpParam;

        // todo тут похоже что нужно выбрать приоритет взависимости от того какой обьем больше на покупку или продажу
        boolean isBuyMore = volBuyLastBar > volSellLastBar;
        // Протестироал, с этим флагом результаты значительно улучшились

        Double rsiValue = null;
        if (isTestStrategy) {
            Map<Integer, Double> integerDoubleMap = MAP_DATE_MAP_WINDOW_SIZE_RSI_VALUE.get(lastBar.getCreateDate());
            if (nonNull(integerDoubleMap)) {
                rsiValue = integerDoubleMap.get(MAP_STRATEGY_WINDOW_SIZE_RSI.get(strategy));
            }
        } else {
            Integer windowSize = MAP_STRATEGY_WINDOW_SIZE_RSI.get(strategy);
            List<Bar> lastBarBySymbolAndByCount = barService.findLastBarBySymbolAndByCount(symbol.name(), lastBar.getCreateDate(), windowSize)
                    .stream().sorted(Comparator.comparing(Bar::getCreateDate)).collect(Collectors.toList());
            rsiValue = Rsi.getValue(lastBarBySymbolAndByCount, windowSize);
        }

        if (rsiValue == null) return;

        if (rsiValue > 80) {
            getVolPosition(symbol);
            Deal createDeal;

            if (strategy.equals("8") || strategy.equals("9")) {
                openPrice = openPrice + shift;
                createDeal = createDeal(lastBar, openPrice, Side.SELL, openPrice + sl, openPrice - tp, volPosition, symbol);
            } else {
                openPrice = openPrice - shift;
                createDeal = createDeal(lastBar, openPrice, Side.BUY, openPrice - sl, openPrice + tp, volPosition, symbol);
            }
            if (isTestStrategy) {
                deals.add(createDeal);
            } else {
                openOrder(createDeal);
            }
            return;
        }

        if (rsiValue < 20) {
            getVolPosition(symbol);
            Deal createDeal;

            if (strategy.equals("8") || strategy.equals("9")) {
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
    // todo попробовать постепенно увеличивать коэффициент
    private void getVolPosition(Symbol symbol) {
        final int requiredDealsCount = 2;

        List<Deal> recentDeals = new ArrayList<>();
        // Получаем 3 последние сделки
        if (isTestStrategy) {
            List<Deal> collect = deals.stream().filter(d -> d.getStrategy().equals(strategy)).collect(Collectors.toList());
            if (collect.size() >= requiredDealsCount) {
                recentDeals = collect.subList(collect.size() - requiredDealsCount, collect.size());
            }
        } else {
            recentDeals = dealService.getDealsStrategy(requiredDealsCount, strategy, symbol.name());

        }

        // Группируем сделки по объему (vol)
        Map<Double, List<Deal>> dealsByVolume = recentDeals.stream()
                .collect(Collectors.groupingBy(Deal::getVol));

        // Если все сделки имеют одинаковый объем
        if (dealsByVolume.size() == 1 && recentDeals.size() == requiredDealsCount) {
            // Проверяем, что все сделки убыточные
            boolean allDealsAreLoss = recentDeals.stream()
                    .allMatch(deal -> deal.getResult() < 0);

            if (allDealsAreLoss) {
                volPosition = recentDeals.get(0).getVol() * coefficient;
            }
        } else {
            if (CollectionUtils.isEmpty(recentDeals)) {
                return;
            }
            // Если объемы разные, работаем с последней сделкой
//            todo check for real
            Deal lastDeal = recentDeals.get(recentDeals.size() - 1);
            if (lastDeal.getResult() < 0) {
                volPosition = lastDeal.getVol();
            }
            // Если последняя сделка прибыльная - volPosition не меняется
        }

        volPosition = changeDoubleValue(volPosition, 10.0);
    }

    @Timed
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
        double shift = 2;
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
                .plusMinutes(minute);
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
        deal.setCurrentResult(result * deal.getVol());

        resultBalance = resultBalance.add(BigDecimal.valueOf(result).multiply(BigDecimal.valueOf(deal.getVol())));
        if (!isTestStrategy) {
            dealService.save(deal);
        }
    }

    private void openOrder(Deal deal) {
        changeDoubleValue(deal);
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

    public static double changeDoubleValue(double value, double round) {
        return Math.round(value * ((int) round)) / round;
    }

    UUID openOrder(double size, Side side, String tvh, String sl, String tp, Symbol symbol, PositionIdx hedgeMode) {
        String key = account.getKey();
        String secret = account.getSecret();

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

    /**
     * Проверка на наличие открытых позиций
     *
     * @return TRUE - если нет открытых позиций
     */
    private boolean isNotPosition(Deal deal) {
        String key = account.getKey();
        String secret = account.getSecret();

        boolean isOpen = CommonUtils.isOpenPosition(key, secret, deal);

        return !isOpen;
    }

    private void fillRsiMap(List<Bar> bars) {
        bars = bars.stream().sorted(Comparator.comparing(Bar::getCreateDate)).collect(Collectors.toList());

        int windowSize = 13;
        for (int i = windowSize; i < bars.size() - 1; i++) {
            List<Bar> subList13 = safeGetWindow(i, 13, bars);
            List<Bar> subList6 = safeGetWindow(i, 6, bars);
            double rsiValue13 = Rsi.getValue(subList13, windowSize);
            double rsiValu6 = Rsi.getValue(subList6, windowSize);

            Map<Integer, Double> hashMap = Map.of(
                    6, rsiValu6,
                    13, rsiValue13
            );
            MAP_DATE_MAP_WINDOW_SIZE_RSI_VALUE.put(bars.get(i).getCreateDate(), hashMap);
        }
    }

    List<Bar> safeGetWindow(int index, int windowSize, List<Bar> source) {
        int start = Math.max(0, index - windowSize);
        return source.subList(start, index);
    }

}
