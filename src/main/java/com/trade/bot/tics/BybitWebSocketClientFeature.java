package com.trade.bot.tics;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.trade.bot.dto.FutureBybitTradeDto;
import com.trade.bot.entity.Tick;
import com.trade.bot.enums.OrderSide;
import com.trade.bot.enums.Symbol;
import com.trade.bot.enums.TradeLastTick;
import com.trade.bot.repository.TickRepository;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Slf4j
public class BybitWebSocketClientFeature extends MainWebSocketClient {

    private final List<Tick> ticks;

    public BybitWebSocketClientFeature(TickRepository tickRepository, ObjectMapper objectMapper) throws URISyntaxException {
        super("wss://stream.bybit.com/contract/usdt/public/v3", tickRepository, objectMapper);
        this.ticks = new ArrayList<>();
    }

    @Override
    @SneakyThrows
    public void onMessage(String message) {
        // Обработка входящего сообщения
        FutureBybitTradeDto futureBybitTradeDto = null;
        try {
            futureBybitTradeDto = objectMapper.readValue(message, FutureBybitTradeDto.class);
        } catch (Exception e) {
            log.error(e.getMessage());
            return;
        }

        if (isNull(futureBybitTradeDto) || isNull(futureBybitTradeDto.getData())) {
            return;
        }

        futureBybitTradeDto.getData().forEach(el -> {
            String q = el.getV();
            String operation = el.getOperation();
            String p = el.getP();
            String symbol = el.getSymbol();

            double count = Double.parseDouble(q);

            Tick tick = Tick.builder()
                    .quantity(q)
                    .price(p)
                    .side(OrderSide.valueOf(operation))
                    .createDate(getDateTime(el.getTime()))
                    .symbol(Symbol.valueOf(symbol.replace("USDT", "")))
                    .lastTick(TradeLastTick.valueOf(el.getTradeLastTick()))
                    .exchange("bybit")
                    .instrument("features")
                    .build();

            ticks.add(tick);

            if (ticks.size() > 1) {
                tickRepository.saveAll(ticks);
                ticks.clear();
            }
        });
    }

    @Override
    @SneakyThrows
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("Соединение закрыто: " + reason);
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        System.out.println("Соединение установлено");
        subscribe(); // Пример: подписка на свечные данные BTCUSDT с периодом 1 минута
    }

    private void subscribe() {
        JSONObject request = new JSONObject();
        request.put("op", "subscribe");
        request.put("args", new String[]{"publicTrade.WLDUSDT"});
        send(request.toString());
    }
}
