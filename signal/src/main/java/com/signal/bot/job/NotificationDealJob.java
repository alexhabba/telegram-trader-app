package com.signal.bot.job;

import com.dao.bot.entity.Deal;
import com.dao.bot.enums.Status;
import com.dao.bot.enums.Symbol;
import com.dao.bot.service.DealService;
import com.signal.bot.config.TelegramBot;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.dao.bot.enums.Status.CANCEL;
import static com.dao.bot.enums.Status.PROCESSING;

@Service
@RequiredArgsConstructor
public class NotificationDealJob {

    private final TelegramBot telegramBot;
    private final DealService dealService;
    private final Map<UUID, Deal> MAP_UUID_DEAL = new HashMap<>();

    @Scheduled(cron = "06 * * * * *")
    public void checkDeal() {
        if (!MAP_UUID_DEAL.isEmpty()) {
            MAP_UUID_DEAL.values().stream()
                    .map(el -> dealService.getById(el.getId()))
                    .map(Deal::getStatus)
                    .findFirst()
                    .ifPresent(el -> {
                        if (CANCEL == el) {
                            telegramBot.prepareAndSendMessage(-1002294735226L, "Отмена лимитного ордера для WLD");
                            MAP_UUID_DEAL.clear();
                        } else if (PROCESSING == el) {
                            MAP_UUID_DEAL.clear();
                        }
                    });
        } else {
            List<Deal> deals = dealService.getLastDealByStatusAndBySymbol(Status.STARTED, Symbol.SOL);
//          https://www.bybit.com/en/dashboard/?ref=N1GZ5V
//          https://www.bybit.com/trade/usdt/WLDUSDT
            if (!deals.isEmpty() && !MAP_UUID_DEAL.containsKey(deals.get(0).getId())) {
                Deal lastDeal = deals.get(0);
                MAP_UUID_DEAL.put(lastDeal.getId(), lastDeal);
                var answer = new StringBuilder("Регистрации на байбит\nhttps://www.bybit.com/en/dashboard/?ref=N1GZ5V\n\n");
                answer.append(lastDeal.getSide()).append("Limit ").append(lastDeal.getSymbol()).append("\nTVH = ").append(lastDeal.getOpen()).append("\nSTOP = ").append(lastDeal.getSl()).append("\nTP = ").append(lastDeal.getTp())
                        .append("\nhttps://www.bybit.com/trade/usdt/")
                        .append(lastDeal.getSymbol().name()).append("USDT");

                telegramBot.prepareAndSendMessage(-1002294735226L, answer.toString());
            }

        }
    }

}
