package com.trade.bot.job;

import com.trade.bot.dto.bybit.ResponsePosition;
import com.trade.bot.enums.OrderType;
import com.trade.bot.enums.Owner;
import com.trade.bot.enums.Side;
import com.trade.bot.enums.Symbol;
import com.trade.bot.service.bybit.BybitBalanceService;
import com.trade.bot.service.bybit.BybitOrderService;
import com.trade.bot.service.bybit.BybitPositionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.trade.bot.enums.Owner.MAIN_BYBIT;
import static java.math.RoundingMode.CEILING;
import static java.util.Objects.isNull;

/**
 * Класс отвечает за то, чтобы на всех аккаунтах было одинаковое количество контрактов одинаковый стоп лосс и тейк профит
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MonitoringPosition {

    @Value("#{${accounts}}")
    private Map<Owner, Map<String, String>> keySecretMap;

    private final BybitPositionService bybitPositionService;
    private final BybitOrderService bybitOrderService;
    private final BybitBalanceService bybitBalanceService;

//    @Scheduled(fixedRate = 13000)
    public void checkPositions() {

        // get balance
        keySecretMap.forEach(((owner, stringStringMap) -> stringStringMap.forEach((key, secret) -> {
            BigDecimal balance = bybitBalanceService.getBalance(key, secret);
            log.info("{}  {}", owner, balance);
        })));

        System.out.println();
        Map<Owner, ResponsePosition.Position> mapOwnerPositionMain = new HashMap<>();
        Map<Owner, ResponsePosition.Position> mapOwnerPosition = new HashMap<>();
        keySecretMap.forEach((k, v) -> {
            v.forEach((key, secret) -> {
                ResponsePosition.Position position = bybitPositionService.getPosition(key, secret).getResult().getPositions().get(0);
                if (k == MAIN_BYBIT) {
                    mapOwnerPositionMain.put(k, position);
                } else {
                    mapOwnerPosition.put(k, position);
                }
            });
        });

        ResponsePosition.Position mainPos = mapOwnerPositionMain.get(MAIN_BYBIT);
        BigDecimal mainSize = mainPos.getSize();

        // если позиций нет то нужно завершить джобу предварительно проверить их отсутствие на других субаккаунтах
        if (mainSize.equals(BigDecimal.ZERO)) {
            // Проверка позиций и их закрытие если они есть
            keySecretMap.forEach((k, v) -> {
                v.forEach((key, secret) -> {
                    if (k != MAIN_BYBIT) {
                        closePositionIfExist(key, secret, mapOwnerPosition.get(k));
                    }
                });
            });
            return;
        }

        // если есть позиция на основном аккаунте то проверяем есть ли стоп лосс
        if (isNull(mainPos.getStopLoss())) {
            keySecretMap.get(MAIN_BYBIT).forEach((key, secret) -> sendSlTp(key, secret, mainPos));
        }

        // если нет позиций на суббаккаунтах то открываем их
        // тут еще реализовано добавление или удаление части позиций
        mapOwnerPosition.forEach(((owner, position) -> {
            if (!position.getSize().equals(mainSize)) {
                keySecretMap.get(owner).forEach((key, secret) -> openOrAddOrDeletePosition(key, secret, mainPos, position.getSize()));
            }
        }));

        // Проверка на суббаккаунтах соответствия st and tp
        mapOwnerPosition.forEach((owner, position) -> {
            keySecretMap.get(owner).forEach((key, secret) -> checkStTp(key, secret, mainPos, position));
        });
    }

    private void checkStTp(String key, String secret, ResponsePosition.Position mainPos, ResponsePosition.Position subPos) {
        if (isNull(subPos.getStopLoss()) || !subPos.getStopLoss().equals(mainPos.getStopLoss()) ||
                !subPos.getTakeProfit().equals(mainPos.getTakeProfit())) {
            bybitPositionService.setSlTp(key, secret, mainPos.getStopLoss(), mainPos.getTakeProfit());
        }
    }

    // если нет позиций на суббаккаунтах то открываем их
    // тут еще реализовано добавление или удаление части позиций
    private void openOrAddOrDeletePosition(String key, String secret, ResponsePosition.Position mainPos, BigDecimal subSize) {
        // было 3 и добавили еще 3
        // а в саб осталось 3
        // main - sub = q

        BigDecimal mainSize = mainPos.getSize();
        BigDecimal size = mainSize.subtract(subSize);
        Side side;

        if (size.compareTo(BigDecimal.ZERO) > 0) {
            side = Side.valueOf(mainPos.getSide());
        } else {
            side = Side.valueOf(mainPos.getSide()) == Side.Buy ? Side.Sell : Side.Buy;
            size = subSize.subtract(mainSize);
        }
        // если покупка то
        bybitOrderService.openOrder(
                key,
                secret,
                Symbol.WLD,
                "0",
                "0",
                size.toString(),
                side,
                OrderType.MARKET,
                UUID.randomUUID(),
                System.out::println);
    }

    private void closePositionIfExist(String key, String secret, ResponsePosition.Position krisPos) {
        if (!krisPos.getSize().equals(BigDecimal.ZERO)) {
            Side side = Side.valueOf(krisPos.getSide()) == Side.Buy ? Side.Sell : Side.Buy;
            bybitOrderService.openOrder(
                    key,
                    secret,
                    Symbol.WLD,
                    "0",
                    "0",
                    krisPos.getSize().toString(),
                    side,
                    OrderType.MARKET,
                    UUID.randomUUID(),
                    System.out::println);
        }
    }

    private void sendSlTp(String key, String secret, ResponsePosition.Position pos) {
        // вычисляем 1 процент от средней цены входа
        BigDecimal onePercent = pos.getAvgPrice().divide(BigDecimal.valueOf(100), 5, CEILING);
        // пока 2 %
        onePercent = onePercent.multiply(BigDecimal.valueOf(2));
        BigDecimal sl = pos.getSide().equals(Side.Buy.name()) ? pos.getAvgPrice().subtract(onePercent) : pos.getAvgPrice().add(onePercent);

        if (pos.getSide().equals(Side.Buy.name()) && sl.compareTo(pos.getLiqPrice()) < 0) {
            sl = pos.getLiqPrice();
        }
        if (pos.getSide().equals(Side.Sell.name()) && sl.compareTo(pos.getLiqPrice()) > 0) {
            sl = pos.getLiqPrice();
        }
        BigDecimal tp = pos.getSide().equals(Side.Buy.name()) ?
                pos.getAvgPrice().add(onePercent.multiply(BigDecimal.valueOf(3))) :
                pos.getAvgPrice().subtract(onePercent.multiply(BigDecimal.valueOf(3)));
        bybitPositionService.setSlTp(
                key,
                secret,
                sl.setScale(3, RoundingMode.DOWN),
                tp.setScale(3, RoundingMode.DOWN));
    }

}
