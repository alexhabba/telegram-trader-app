package com.dao.bot.service;

import com.dao.bot.entity.Bar;
import com.dao.bot.repository.BarRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BarDaoService {

    private final BarRepository barRepository;

    public List<Bar> findLastBar(int count) {
      return barRepository.findLastBar(count);
    }

    public List<Bar> findAll() {
        return barRepository.findAll();
    }

    public void deleteAll() {
        barRepository.deleteAll();
    }
}
