package com.trade.bot.service;

import com.trade.bot.dto.FractalDto;
import com.trade.bot.entity.Fractal;
import com.trade.bot.mapper.FractalMapper;
import com.trade.bot.repository.FractalRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FractalService {

    private final FractalRepository fractalRepository;
    private final FractalMapper fractalMapper;

    public List<FractalDto> findAll() {
        List<Fractal> fractals = fractalRepository.findAll().stream()
                .sorted(Comparator.comparing(Fractal::getCreateDate))
                .collect(Collectors.toList());

        return fractalMapper.toDto(fractals);
    }

    public List<FractalDto> findAllLimit(int size) {
        List<Fractal> fractals = fractalRepository.findAll().stream()
                .sorted(Comparator.comparing(Fractal::getCreateDate))
                .collect(Collectors.toList());
        fractals = fractals.stream()
                .skip(fractals.size() - size)
                .collect(Collectors.toList());

        return fractalMapper.toDto(fractals);
    }
}
