package com.trade.bot.service.bybit;

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
import java.util.List;
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
public class BybitOrderService {

    private final ObjectMapper objectMapper;

    public void openOrder(String key, String secret, Symbol symbol, String tvh, String stop, String qty,
                          Side side, OrderType orderType, UUID orderLinkId, BybitApiCallback<Object> callback) {
        try {
            var client = BybitApiClientFactory.newInstance(key, secret, BybitApiConfig.MAINNET_DOMAIN, true).newAsyncTradeRestClient();
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

    private String getQty(String tvhh, String stopp) {
//        BigDecimal percent = BigDecimal.valueOf(13);
//        BigDecimal percent100 = BigDecimal.valueOf(100);

//        BigDecimal capital = getBalance("", "");

//        BigDecimal tvh = BigDecimal.valueOf(Double.parseDouble(tvhh));
//        BigDecimal stop = BigDecimal.valueOf(Double.parseDouble(stopp));
//
//        BigDecimal subtract = tvh.subtract(stop);
//        double countContract = Math.abs(capital.multiply(percent).divide(percent100, 5, CEILING).divide(subtract, 5, CEILING).doubleValue());
//        System.out.println(countContract);
//        return String.valueOf(countContract > 1 ? (int)countContract : countContract);
        return null;
    }

}
