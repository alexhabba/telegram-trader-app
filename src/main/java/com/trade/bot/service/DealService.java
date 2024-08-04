package com.trade.bot.service;

import com.trade.bot.entity.Deal;
import com.trade.bot.repository.DealRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DealService {

    private final DealRepository dealRepository;

    public Deal getLastDeal() {
        List<Deal> deals = dealRepository.findLastDeal(1);
        return deals.isEmpty() ? null : deals.get(0);
    }

    public void save(Deal deal) {
        dealRepository.save(deal);
    }

    public List<Deal> getAllDeals() {
        return dealRepository.findAll();
    }

    public void saveAll(List<Deal> deals) {
        dealRepository.saveAll(deals);
    }

    public void deleteAll() {
        dealRepository.deleteAll();
    }
}
