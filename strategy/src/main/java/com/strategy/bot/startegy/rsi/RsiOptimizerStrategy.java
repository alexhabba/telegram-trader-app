package com.strategy.bot.startegy.rsi;

import com.bybit.api.client.domain.trade.Side;
import com.dao.bot.entity.Bar;
import com.dao.bot.entity.Deal;
import com.dao.bot.entity.Statistic;
import com.dao.bot.enums.Symbol;
import com.dao.bot.repository.StatisticRepository;
import com.dao.bot.service.DealService;
import com.strategy.bot.dto.ResponsePosition;
import com.strategy.bot.service.BybitOrderService;
import com.strategy.bot.service.BybitPositionService;
import com.strategy.bot.startegy.test.WrapperBalance;
import com.strategy.bot.startegy.test.WrapperDouble;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import static com.dao.bot.enums.Status.*;
import static java.util.Objects.nonNull;

/**
 *
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RsiOptimizerStrategy {

    @Value("${isTestStrategy}")
    private boolean isTestStrategy;

    @Value("${strategy}")
    private String strategy;

    private final StatisticRepository statisticRepository;
    private final AtomicInteger atomicInteger = new AtomicInteger();
    @Value("${start-vol}")
    private int startVol;

    private double maxVol = 30_000;
    private final ThreadLocal<WrapperDouble> maxVolInStrategy = new ThreadLocal<>();
    private final ThreadLocal<WrapperBalance> resultBalance = new ThreadLocal<>();

    private final DealService dealService;
    private final BybitOrderService bybitOrderService;
    private final BybitPositionService positionService;
    private final ThreadLocal<LinkedList<Deal>> deals = new ThreadLocal<>();

    public void execute(Bar lastBar, double shift, double slTemp, double tpTemp, String strategy, LinkedList<Deal> list,
                        double maxVol, int min,
                        int count,
                        WrapperDouble maxVolInStrategyWrapper, WrapperBalance resultBalanceWrapper, LocalDateTime lastLocalDateTime, int windowSizeRsi, double rsiValue) {

        maxVolInStrategy.set(maxVolInStrategyWrapper);
        deals.set(list);
        resultBalance.set(resultBalanceWrapper);
        if (isTestStrategy && lastLocalDateTime.minusMinutes(1).equals(lastBar.getCreateDate())) {

            double commonResult = 0;
            double result = 0;
            long badCount = 0;
            long successCount = 0;

            LinkedList<Deal> dealsList = deals.get();
            for (int i = 0; i < dealsList.size() - 1; i++) {
                Deal deal = dealsList.get(i);
                commonResult += deal.getResult() * deal.getVol() - deal.getVol() * 0.0015;
                result += deal.getResult();
                badCount += deal.getResult() < 0 ? 1 : 0;
                successCount += deal.getResult() > 0 ? 1 : 0;
            }

            if (result > 0) {
                Statistic statistic = Statistic.builder()
                        .id(UUID.randomUUID())
                        .maxVolInStrategy(maxVolInStrategy.get().getValue())
                        .min(min)
                        .vol(maxVol)
                        .shift(shift)
                        .sl(slTemp)
                        .tp(tpTemp)
                        .strategy(strategy)
                        .badCount(badCount)
                        .successCount(successCount)
                        .result(result)
                        .commonResult(commonResult)
                        .rsiInterval(windowSizeRsi)
                        .build();

                statisticRepository.save(statistic);
            }
        }

        // Посмотреть на последнюю сделку по времени
        Deal lastDeal = null;

        if (isTestStrategy) {
            if (!deals.get().isEmpty()) {
                lastDeal = deals.get().getLast();
            }
        } else {
            lastDeal = dealService.getLastDealStrategy(strategy, lastBar.getSymbol().name());
        }

        // Если есть не завершенная сделка то проверяем закрылась она или нет
        if (nonNull(lastDeal) && lastDeal.getStatus() == PROCESSING) {
            checkTpSl(lastBar, lastDeal);
            return;
        }

        if (nonNull(lastDeal) && lastDeal.getStatus() == STARTED) {
            if (isTestStrategy) {
                if (isOpenPosition(lastBar, lastDeal)) {
                    // todo если была открыта любая позиция открытая не ботом то переведет в статус PROCESSING
                    lastDeal.setStatus(PROCESSING);
                    return;
                } else if (isCancelPosition(lastBar, lastDeal, min)) {
                    return;
                }
            }
            return;
        }

        if (nonNull(lastDeal) && lastDeal.getOpenDate().plusMinutes(13).isAfter(lastBar.getCreateDate())) {
            return;
        }

        double openPrice = lastBar.getClose();
        double onePercent = openPrice / 100;
        double sl = onePercent * slTemp;
        double tp = onePercent * tpTemp;
        double vol = 0.3;

        if (rsiValue > 80) {
            Deal createDeal;
            if (strategy.equals("8")) {
                openPrice = openPrice + shift;
                createDeal = createDeal(lastBar, openPrice, Side.SELL, openPrice + sl, openPrice - tp, vol);
            } else {
                openPrice = openPrice - shift;
                createDeal = createDeal(lastBar, openPrice, Side.BUY, openPrice - sl, openPrice + tp, vol);
            }
            deals.get().add(createDeal);
        }

        if (rsiValue < 20) {
            Deal createDeal;

            if (strategy.equals("8")) {
                openPrice = openPrice - shift;
                createDeal = createDeal(lastBar, openPrice, Side.BUY, openPrice - sl, openPrice + tp, vol);
            } else {
                openPrice = openPrice + shift;
                createDeal = createDeal(lastBar, openPrice, Side.SELL, openPrice + sl, openPrice - tp, vol);
            }

            deals.get().add(createDeal);
        }

    }

    private Deal createDeal(Bar lastBar, double openPrice, Side sell, double sl, double tp, double vol) {
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
        WrapperDouble wrapperDouble = maxVolInStrategy.get();
        if (wrapperDouble.getValue() < vol) {
            wrapperDouble.setValue(vol);
        }
        return createDeal;
    }

    private void checkTpSl(Bar bar, Deal deal) {
        double low = bar.getLow();
        double high = bar.getHigh();

        if (deal.getSide() == Side.BUY) {
            if (low <= deal.getSl()) {
                // закрытие по стоп лосс
                commonCloseAction(deal, bar, deal.getSl(), deal.getSl() - deal.getOpen());
            }

            if (high >= deal.getTp()) {
                // закрытие по тейк профит
                commonCloseAction(deal, bar, deal.getTp(), deal.getTp() - deal.getOpen());
            }
        }

        if (deal.getSide() == Side.SELL) {
            if (high >= deal.getSl()) {
                // закрытие по стоп лосс
                commonCloseAction(deal, bar, deal.getSl(), deal.getOpen() - deal.getSl());
            }

            if (low <= deal.getTp()) {
                // закрытие по тейк профит
                commonCloseAction(deal, bar, deal.getTp(), deal.getOpen() - deal.getTp());
            }
        }

    }

    private boolean isCancelPosition(Bar bar, Deal deal, int min) {
        LocalDateTime openDate = deal.getOpenDate()
                .plusMinutes(min);
        LocalDateTime createDate = bar.getCreateDate();

        if (createDate.isAfter(openDate)) {
            deal.setStatus(CANCEL);
            deals.get().remove(deal);
            return true;
        }
        return false;
    }

    private boolean isOpenPosition(Bar bar, Deal deal) {
        double low = bar.getLow();
        double high = bar.getHigh();

        if (deal.getSide() == Side.BUY && low <= deal.getOpen()) {
            return true;
        } else return deal.getSide() == Side.SELL && high >= deal.getOpen();
    }

    private void commonCloseAction(Deal deal, Bar bar, double close, double result) {
        if (!isTestStrategy && !isNotPosition()) return;
        deal.setCloseDate(bar.getCreateDate().plusMinutes(1));
        deal.setStatus(COMPLETED);
        deal.setClose(close);
        deal.setResult(result);

        BigDecimal add = resultBalance.get().getBalance().add(BigDecimal.valueOf(result).multiply(BigDecimal.valueOf(deal.getVol())));
        WrapperBalance wrapperBalance = resultBalance.get();
        wrapperBalance.setBalance(add);
        resultBalance.set(wrapperBalance);
        if (!isTestStrategy) {
            dealService.save(deal);
        }
    }

    private boolean isNotPosition() {
        ResponsePosition position = positionService.getPosition("key", "secret", Symbol.SOL);
        BigDecimal size = position.getResult().getPositions().get(0)
                .getSize();
        return size.equals(BigDecimal.ZERO);
    }

}
