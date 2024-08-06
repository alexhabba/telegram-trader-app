package com.dao.bot.service;

import com.dao.bot.repository.FractalRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FractalDaoService {

    private final FractalRepository fractalRepository;

}
