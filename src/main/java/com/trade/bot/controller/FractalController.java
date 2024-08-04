package com.trade.bot.controller;

import com.trade.bot.dto.FractalDto;
import com.trade.bot.service.FractalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@CrossOrigin
@RestController
@RequiredArgsConstructor
public class FractalController {

    @Autowired
    private FractalService fractalService;

    @GetMapping("/getFractalAll")
    public ResponseEntity<List<FractalDto>> findAll() {
        return ResponseEntity.ok(fractalService.findAll());
    }

    @GetMapping("/fractalSize")
    public ResponseEntity<List<FractalDto>> findAllLimit(@RequestParam int size) {
        return ResponseEntity.ok(fractalService.findAllLimit(size));
    }
}
