package com.trade.bot.job;

import com.trade.bot.entity.Bar;
import com.trade.bot.entity.Fractal;
import com.trade.bot.repository.BarRepository;
import com.trade.bot.repository.FractalRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class FractalJob {

    private final FractalRepository fractalRepository;
    private final BarRepository barRepository;

    @Scheduled(fixedDelay = 60000)
    @SneakyThrows
    private void someMethod() {
        // чтобы сформировать фрактал, нужно слева и справа иметь одинаковое кол-во баров
        // фрактал 13 баров берем кол-во баров умножаем на 2 и плюс 2
        int count = 28;
        List<Bar> bars = barRepository.findLastBar(count, "TON").stream()
                .filter(Objects::nonNull)
                .sorted(comparing(Bar::getCreateDate))
                .limit(count - 1)
                .collect(toList());

        int median = bars.size() / 2;
        Bar bar = bars.get(median);

        if (isLowFractalLeft(bars, median) && isLowFractalRight(bars, median)) {
            Fractal fractal = Fractal.builder()
                    .countBar(count)
                    .createDate(bar.getCreateDate())
                    .interval(60)
                    .low(bar.getLow())
                    .build();
            fractalRepository.save(fractal);
        }

        if (isHighFractalLeft(bars, median) && isHighFractalRight(bars, median)) {
            Fractal fractal = Fractal.builder()
                    .countBar(count)
                    .createDate(bar.getCreateDate())
                    .interval(60)
                    .high(bar.getHigh())
                    .build();
            fractalRepository.save(fractal);
        }
    }

    private boolean isLowFractalLeft(List<Bar> bars, int median) {
        for (int i = 0; i < median; i++) {
            if (!(Double.parseDouble(bars.get(median).getLow()) <= Double.parseDouble(bars.get(i).getLow()))) {
                return false;
            }
        }
        return true;
    }

    private boolean isLowFractalRight(List<Bar> bars, int median) {
        for (int i = median + 1; i < bars.size(); i++) {
            if (!(Double.parseDouble(bars.get(median).getLow()) < Double.parseDouble(bars.get(i).getLow()))) {
                return false;
            }
        }
        return true;
    }

    private boolean isHighFractalLeft(List<Bar> bars, int median) {
        for (int i = 0; i < median; i++) {
            if (!(Double.parseDouble(bars.get(median).getHigh()) >= Double.parseDouble(bars.get(i).getHigh()))) {
                return false;
            }
        }
        return true;
    }

    private boolean isHighFractalRight(List<Bar> bars, int median) {
        for (int i = median + 1; i < bars.size(); i++) {
            if (!(Double.parseDouble(bars.get(median).getHigh()) > Double.parseDouble(bars.get(i).getHigh()))) {
                return false;
            }
        }
        return true;
    }
}
