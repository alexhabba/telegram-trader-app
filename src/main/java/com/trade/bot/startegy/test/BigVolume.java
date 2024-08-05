package com.trade.bot.startegy.test;

import com.trade.bot.entity.Bar;
import com.trade.bot.entity.Deal;
import com.trade.bot.enums.OrderType;
import com.trade.bot.enums.Owner;
import com.trade.bot.enums.Side;
import com.trade.bot.enums.Symbol;
import com.trade.bot.service.BarService;
import com.trade.bot.service.DealService;
import com.trade.bot.service.bybit.BybitOrderService;
import com.trade.bot.startegy.StrategyExecutor;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.trade.bot.enums.Owner.KRIS_SUB_FIRST_BYBIT;
import static com.trade.bot.enums.Owner.KRIS_SUB_SECOND_BYBIT;
import static com.trade.bot.enums.Status.COMPLETED;
import static com.trade.bot.enums.Status.PROCESSING;
import static java.util.Objects.nonNull;

@Service
@RequiredArgsConstructor
public class BigVolume implements StrategyExecutor {

    @Value("#{${accounts}}")
    private Map<Owner, Map<String, String>> keySecretMap;

    private final DealService dealService;
    private final BarService barService;
    private final BybitOrderService bybitOrderService;
    private final LinkedList<Deal> deals = new LinkedList<>();

    @EventListener({ContextRefreshedEvent.class})
    @SneakyThrows
    public void init() {
//        dealService.deleteAll();
//        openOrder("5", Side.Sell, "1.8", "1.5");
        System.out.println();
    }

    // 6.11618542 test 5 август
    @Scheduled(fixedDelay = 3000)
    public void execute() {
        List<Bar> lastBars = barService.findLastBar(1);
        Bar lastBar = lastBars.get(0);

        double volBuyLastBar = Double.parseDouble(lastBar.getVolBuy());
        double volSellLastBar = Double.parseDouble(lastBar.getVolSell());
        double closeLastBar = Double.parseDouble(lastBar.getClose());
        double openBuyLastBar = Double.parseDouble(lastBar.getOpen());

        // Посмотреть на последнюю сделку по времени
        Deal lastDeal = dealService.getLastDeal();
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
        double sl = onePercent * 2;
        double tp = onePercent * 10;
        double vol = nonNull(lastDeal) && lastDeal.getResult() < 0 ? (int) (lastDeal.getVol() * 1.3) + 13 : 13;

        if (volBuyLastBar > 150_000 && closeLastBar < openBuyLastBar) {
            Deal createDeal = Deal.builder()
                    .openDate(lastBar.getCreateDate().plusMinutes(1))
                    .open(openPrice)
                    .side(Side.Sell)
                    .sl(openPrice + sl)
                    .tp(openPrice - tp)
                    .vol(vol)
                    .build();
            // открытие и сохранение сделки в БД
            openOrder(
                    Double.toString(createDeal.getVol()),
                    createDeal.getSide(),
                    Double.toString(createDeal.getSl()),
                    Double.toString(createDeal.getTp())
            );
            dealService.save(createDeal);
        }

        if (volSellLastBar > 150_000 && closeLastBar > openBuyLastBar) {
            Deal createDeal = Deal.builder()
                    .openDate(lastBar.getCreateDate().plusMinutes(1))
                    .open(openPrice)
                    .side(Side.Buy)
                    .sl(openPrice - sl)
                    .tp(openPrice + tp)
                    .vol(vol)
                    .build();

            // открытие и сохранение сделки в БД
            openOrder(
                    Double.toString(createDeal.getVol()),
                    createDeal.getSide(),
                    Double.toString(createDeal.getSl()),
                    Double.toString(createDeal.getTp())
            );
            dealService.save(createDeal);
        }

    }

    @Override
    public void execute(Bar lastBar) {

        if (LocalDateTime.now().minusHours(3).minusMinutes(5).withSecond(0).withNano(0).isBefore(lastBar.getCreateDate())) {
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
        }

        double volBuyLastBar = Double.parseDouble(lastBar.getVolBuy());
        double volSellLastBar = Double.parseDouble(lastBar.getVolSell());
        double closeLastBar = Double.parseDouble(lastBar.getClose());
        double openBuyLastBar = Double.parseDouble(lastBar.getOpen());

        // Посмотреть на последнюю сделку по времени
        Deal lastDeal = null;
        if (!deals.isEmpty()) {
            lastDeal = deals.getLast();
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
        double sl = onePercent * 2;
        double tp = onePercent * 10;
        double vol = nonNull(lastDeal) && lastDeal.getResult() < 0 ? (int) (lastDeal.getVol() * 1.3) + 13 : 13;

        if (volBuyLastBar > 150_000 && closeLastBar < openBuyLastBar) {
            // todo need to think how make return value deal

            Deal createDeal = Deal.builder()
                    .openDate(lastBar.getCreateDate().plusMinutes(1))
                    .open(openPrice)
                    .status(PROCESSING)
                    .side(Side.Sell)
                    .sl(openPrice + sl)
                    .tp(openPrice - tp)
                    .vol(vol)
                    .build();
            // открытие и сохранение сделки в БД
//            dealService.save(createDeal);
            deals.add(createDeal);
        }

        if (volSellLastBar > 150_000 && closeLastBar > openBuyLastBar) {
            Deal createDeal = Deal.builder()
                    .openDate(lastBar.getCreateDate().plusMinutes(1))
                    .open(openPrice)
                    .status(PROCESSING)
                    .side(Side.Buy)
                    .sl(openPrice - sl)
                    .tp(openPrice + tp)
                    .vol(vol)
                    .build();

            // открытие и сохранение сделки в БД
//            dealService.save(createDeal);
            deals.add(createDeal);
        }


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

        dealService.save(deal);
    }

    void openOrder(String size, Side side, String sl, String tp) {
//        String key = new ArrayList<>(keySecretMap.get(KRIS_SUB_SECOND_BYBIT).keySet()).get(0);
//        String secret = keySecretMap.get(KRIS_SUB_FIRST_BYBIT).get(key);
        bybitOrderService.openOrder(
                "9jaVPeAdvHrCmX0ns1",
                "SQnh4QIBRPY7e5ergx66hSox2LtanPfWl4J0",
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
