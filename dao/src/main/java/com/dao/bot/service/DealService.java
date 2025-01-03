package com.dao.bot.service;

import com.dao.bot.entity.Deal;
import com.dao.bot.enums.Status;
import com.dao.bot.enums.Symbol;
import com.dao.bot.repository.DealRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DealService {

    private final DealRepository dealRepository;

    public Deal getLastDeal() {
        List<Deal> deals = dealRepository.findLastDeal(1);
        return deals.isEmpty() ? null : deals.get(0);
    }

    public Deal getLastDealStrategy(String strategy) {
        List<Deal> deals = dealRepository.findLastDealStrategy(1, strategy);
        return deals.isEmpty() ? null : deals.get(0);
    }

    public Deal getLastDealByStatusAndBySymbol() {
        Optional<Deal> deal = dealRepository.findDealByStatusAndSymbol(Status.STARTED, Symbol.WLD);
        return deal.orElse(null);
    }


    public Deal save(Deal deal) {
        return dealRepository.save(deal);
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
