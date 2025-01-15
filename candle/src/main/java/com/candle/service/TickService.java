package com.candle.service;

import lombok.RequiredArgsConstructor;
import okhttp3.OkHttpClient;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TickService {

    private final OkHttpClient client;

}
