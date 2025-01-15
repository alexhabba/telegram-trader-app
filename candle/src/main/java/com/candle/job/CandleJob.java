package com.candle.job;

import com.candle.test.CandleApi;
import com.dao.bot.entity.Bar;
import com.dao.bot.entity.Symbol;
import com.dao.bot.repository.BarRepository;
import com.dao.bot.service.BarService;
import com.dao.bot.service.SymbolService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;

@Service
@RequiredArgsConstructor
public class CandleJob {

    private final BarService barService;
    private final SymbolService symbolService;
    private final ExecutorService executorService;

    @Scheduled(cron = "01 * * * * *")
    public void execute() {
        // todo я хочу получить как минимум за месяц бары(если инструмента нет, то нужно получить по нему историю)
        //  для этого мне нужно добавить в БД таблицу с инструментами(id, symbol)
        //  так же по текущим инструментам необходимо их сохранять в БД(предварительно выяснить сколько купили/продали)
        //  я все же хочу реализовать при запуске этого модуля чтобы наполнилась таблица барами


        List<Symbol> symbols = symbolService.getAllSymbol();
        symbols.stream()
                .map(Symbol::getSymbol)
                .map(com.dao.bot.enums.Symbol::valueOf)
                .forEach(this::execute);
    }

    @SneakyThrows
    public void execute(com.dao.bot.enums.Symbol symbol) {
        LocalDateTime now = LocalDateTime.now();
        ZonedDateTime zonedNow = now.atZone(ZoneId.systemDefault());
        zonedNow = zonedNow.withZoneSameInstant(ZoneId.of("UTC"));


        Bar bar = null;
        try {
            bar = barService.findLastBarBySymbol(symbol.name());
        } catch (EntityNotFoundException e) {
            List<Bar> bars = CandleApi.getCandle(symbol, zonedNow.minusMinutes(3).toLocalDateTime());
            bars.remove(bars.size() - 1);
            barService.saveAll(bars);
            return;
        }

        Bar lastBar = barService.findLastBarBySymbol(symbol.name());
        List<Bar> bars = CandleApi.getCandle(symbol, lastBar.getCreateDate().plusMinutes(1));

        ZonedDateTime finalZonedNow = zonedNow;
        bars.removeIf(b -> finalZonedNow
                .withSecond(0).withNano(0).toLocalDateTime().equals(b.getCreateDate()));

        barService.saveAll(bars);
    }
}
