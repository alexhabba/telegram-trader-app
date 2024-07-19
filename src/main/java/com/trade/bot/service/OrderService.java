package com.trade.bot.service;

import com.bybit.api.client.config.BybitApiConfig;
import com.bybit.api.client.domain.account.AccountType;
import com.bybit.api.client.domain.account.request.AccountDataRequest;
import com.bybit.api.client.exception.BybitApiException;
import com.bybit.api.client.restApi.BybitApiCallback;
import com.bybit.api.client.service.BybitApiClientFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trade.bot.dto.WalletBalanceDto;
import com.trade.bot.entity.Order;
import com.trade.bot.enums.Side;
import com.trade.bot.enums.OrderType;
import com.trade.bot.enums.Status;
import com.trade.bot.enums.Symbol;
import com.trade.bot.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static com.trade.bot.enums.Status.COMPLETED;
import static com.trade.bot.enums.Status.PROCESSING;
import static java.math.RoundingMode.CEILING;
import static java.util.Objects.isNull;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    @Value("${key}")
    private String KEY;
    @Value("${secret}")
    private String SECRET;

    private final ObjectMapper objectMapper;
    private final OrderRepository orderRepository;

    public void openOrder(String tvh, String stop, BybitApiCallback<Object> callback) {
        Order order = findByStatus(PROCESSING);
        String qty = getQty(tvh, stop);
        order.setStatus(COMPLETED);
        order.setQty(qty);
        order.setPrice(tvh);
        save(order);

        openOrder(order.getSymbol(), tvh, stop, qty, order.getSide(), order.getType(), order.getOrderLinkId(), callback);
    }

    public void openOrder(Symbol symbol, String tvh, String stop, String qty,
                          Side side, OrderType orderType, UUID orderLinkId, BybitApiCallback<Object> callback) {
        try {
            var client = BybitApiClientFactory.newInstance(KEY, SECRET, BybitApiConfig.MAINNET_DOMAIN, true).newAsyncTradeRestClient();
            Map<String, Object> order = Map.of(
                    "category", "linear",
                    "symbol", symbol.name() + "USDT",
                    "side", side,
                    "orderType", orderType.getValue(),
                    "qty", qty,
                    "price", tvh,
                    "orderLinkId", orderLinkId.toString(),
                    "stopLoss", stop,
                    "slOrderType", "Market",
                    "tpslMode", "Full"
            );

            client.createOrder(order, callback);
        } catch (BybitApiException e) {
            // Обработка ошибок
            System.err.println("Ошибка: " + e.getMessage());
        }

    }

    @Transactional
    public void save(Order order) {
        orderRepository.save(order);
    }

    public Order findBySymbol(Symbol symbol) {
        return orderRepository.findBySymbol(symbol).orElseThrow(EntityNotFoundException::new);
    }

    @Transactional
    public void updateSymbol(String symbol) {
        Symbol symbolValue = Symbol.valueOf(symbol);
        Order order = findByStatus(PROCESSING);
        order.setSymbol(symbolValue);
        orderRepository.save(order);
    }

    public Order findByStatus(Status status) {
        return orderRepository.findByStatus(status).orElseThrow(EntityNotFoundException::new);
    }

    public boolean orderProcessingAndWithEmptyPriceExist(Status status) {
        Optional<Order> optionalOrder = orderRepository.findByStatus(status).filter(o -> isNull(o.getPrice()));
        return optionalOrder.isPresent();
    }

    private String getQty(String tvhh, String stopp) {
        BigDecimal percent = BigDecimal.valueOf(13);
        BigDecimal percent100 = BigDecimal.valueOf(100);

        BigDecimal capital = getBalance();

        BigDecimal tvh = BigDecimal.valueOf(Double.parseDouble(tvhh));
        BigDecimal stop = BigDecimal.valueOf(Double.parseDouble(stopp));

        BigDecimal subtract = tvh.subtract(stop);
        double countContract = Math.abs(capital.multiply(percent).divide(percent100, 5, CEILING).divide(subtract, 5, CEILING).doubleValue());
        System.out.println(countContract);
        return String.valueOf(countContract > 1 ? (int)countContract : countContract);

    }

    @SneakyThrows
    private BigDecimal getBalance() {
        var client = BybitApiClientFactory.newInstance(KEY, SECRET, BybitApiConfig.MAINNET_DOMAIN, true).newAccountRestClient();
        AccountDataRequest request = AccountDataRequest.builder().accountType(AccountType.UNIFIED).coin("USDT").build();
        Object walletBalance = client.getWalletBalance(request);
        String string = objectMapper.writeValueAsString(walletBalance);
        WalletBalanceDto wallet = objectMapper.readValue(string, WalletBalanceDto.class);
        return wallet.getResult().getList().get(0).getTotalMarginBalance();
    }

}
