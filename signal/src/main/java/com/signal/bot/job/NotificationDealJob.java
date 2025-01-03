package com.signal.bot.job;

import com.dao.bot.entity.Deal;
import com.dao.bot.service.DealService;
import com.signal.bot.config.TelegramBot;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static java.util.Objects.nonNull;

@Service
@RequiredArgsConstructor
public class NotificationDealJob {

    private final TelegramBot telegramBot;
    private final DealService dealService;
    private final Map<UUID, Deal> MAP_UUID_DEAL = new HashMap<>();

    @Scheduled(cron = "06 * * * * *")
    public void checkDeal() {
        // todo необходимо реализовать логику по появлению сделки в бд
        Deal lastDeal = dealService.getLastDealByStatusAndBySymbol();
//        https://www.bybit.com/en/dashboard/?ref=N1GZ5V
//        https://www.bybit.com/trade/usdt/WLDUSDT
        if (nonNull(lastDeal) && !MAP_UUID_DEAL.containsKey(lastDeal.getId())) {
            MAP_UUID_DEAL.put(lastDeal.getId(), lastDeal);
            var answer = new StringBuilder("Регистрации на байбит\nhttps://www.bybit.com/en/dashboard/?ref=N1GZ5V\n\n");
            answer.append(lastDeal.getSide()).append("Limit\nTVH = ").append(lastDeal.getOpen()).append("\nSTOP = ").append(lastDeal.getSl()).append("\nTP = ").append(lastDeal.getTp())
                    .append("\nhttps://www.bybit.com/trade/usdt/")
                    .append(lastDeal.getSymbol().name()).append("USDT");

            telegramBot.prepareAndSendMessage(-1002294735226L, answer.toString());
        }
    }

}
