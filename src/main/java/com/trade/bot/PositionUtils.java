package com.trade.bot;

import com.bybit.api.client.config.BybitApiConfig;
import com.bybit.api.client.domain.CategoryType;
import com.bybit.api.client.domain.position.TpslMode;
import com.bybit.api.client.domain.position.request.PositionDataRequest;
import com.bybit.api.client.domain.trade.PositionIdx;
import com.bybit.api.client.service.BybitApiClientFactory;
import lombok.SneakyThrows;

import java.math.BigDecimal;

public class PositionUtils {

    @SneakyThrows
    public static void sentTpSl(String key, String secret, BigDecimal sl, BigDecimal tp) {
        var client = BybitApiClientFactory.newInstance(key, secret, BybitApiConfig.MAINNET_DOMAIN, true).newAsyncPositionRestClient();
        var setTradingStopRequest = PositionDataRequest.builder()
                .symbol("WLDUSDT")
                .positionIdx(PositionIdx.ONE_WAY_MODE)
                .category(CategoryType.LINEAR)
                .takeProfit(tp.toString())
                .stopLoss(sl.toString())
                .tpslMode(TpslMode.FULL)
                .build();
//        client.setTradingStop(setTradingStopRequest, System.out::println);
        client.setTradingStop(setTradingStopRequest, response -> {});
    }
}
