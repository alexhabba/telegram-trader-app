package com.dao.bot.service;

import com.dao.bot.entity.Deal;
import com.dao.bot.enums.Status;
import com.dao.bot.enums.Symbol;
import com.dao.bot.repository.DealRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DealService {

    private final DealRepository dealRepository;

    public Deal getLastDeal() {
        List<Deal> deals = dealRepository.findLastDeal(1);
        return deals.isEmpty() ? null : deals.get(0);
    }

    public Deal getLastDealStrategy(String strategy, String symbol) {
        List<Deal> deals = dealRepository.findLastDealStrategy(1, strategy, symbol);
        return deals.isEmpty() ? null : deals.get(0);
    }

    public List<Deal> getLastDealByStatusAndBySymbol(Status status, Symbol symbol) {
        return dealRepository.findDealByStatusAndSymbol(status, symbol);
    }

    public Deal getById(UUID id) {
        return dealRepository.findById(id).orElse(null);
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
