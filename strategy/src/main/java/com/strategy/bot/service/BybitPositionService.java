package com.strategy.bot.service;

import com.bybit.api.client.config.BybitApiConfig;
import com.bybit.api.client.domain.CategoryType;
import com.bybit.api.client.domain.position.request.PositionDataRequest;
import com.bybit.api.client.service.BybitApiClientFactory;
import com.dao.bot.enums.Symbol;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.strategy.bot.dto.ResponsePosition;
import com.strategy.bot.utils.PositionUtils;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BybitPositionService {

    private final ObjectMapper objectMapper;

    @SneakyThrows
    public ResponsePosition getPosition(String key, String secret, Symbol symbol) {
        var client = BybitApiClientFactory.newInstance(key, secret, BybitApiConfig.MAINNET_DOMAIN, true).newPositionRestClient();
        Object response = client.getPositionInfo(PositionDataRequest.builder().category(CategoryType.LINEAR).symbol(symbol.name() + "USDT").build());
        return objectMapper.readValue(objectMapper.writeValueAsString(response), ResponsePosition.class);
    }

    @SneakyThrows
    public void setSlTp(String key, String secret, BigDecimal sl, BigDecimal tp) {
        PositionUtils.sentTpSl(key, secret, sl, tp, Symbol.SOL);
    }

    public static void main(String[] args) {
        BybitPositionService bybitPositionService = new BybitPositionService(new ObjectMapper());
        ResponsePosition position = bybitPositionService.getPosition("Bm93uykPRKyNZqaGeI", "NLrdAqquHmoCjxXU3ynmx6f4XypEq5gOufMe", Symbol.SOL);
        System.out.println(position);
    }
}
//Position{symbol='SOLUSDT', leverage='100', autoAddMargin=0, avgPrice=146.76, liqPrice=null, riskLimitValue='50000', takeProfit=null, positionValue='14.676', isReduceOnly=false, tpslMode='Full', riskId=281, trailingStop='0', unrealisedPnl='-0.011', markPrice='146.87', adlRankIndicator=0, cumRealisedPnl='-22.32050068', positionMM='0.02964552', createdTime='1733169914762', positionIdx=2, positionIM='0.17640552', seq=210657888573, updatedTime='1751795788474', side='Sell', bustPrice='', positionBalance='0', leverageSysUpdatedTime='', curRealisedPnl='-0.014676', size=0.1, positionStatus='Normal', mmrSysUpdatedTime='', stopLoss=null, tradeMode=0, sessionAvgPrice=''}
//Position{symbol='SOLUSDT', leverage='100', autoAddMargin=0, avgPrice=146.97, liqPrice=null, riskLimitValue='50000', takeProfit=null, positionValue='14.697', isReduceOnly=false, tpslMode='Full', riskId=281, trailingStop='0', unrealisedPnl='-0.01', markPrice='146.87', adlRankIndicator=0, cumRealisedPnl='0.07098046', positionMM='0.02910006', createdTime='1733169914762', positionIdx=1, positionIM='0.02910006', seq=210657688522, updatedTime='1751795581469', side='Buy', bustPrice='', positionBalance='0', leverageSysUpdatedTime='', curRealisedPnl='-0.014697', size=0.1, positionStatus='Normal', mmrSysUpdatedTime='', stopLoss=null, tradeMode=0, sessionAvgPrice=''}
