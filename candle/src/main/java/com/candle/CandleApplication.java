package com.candle;

import com.dao.bot.DaoScanMarker;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackageClasses = {
        CandleApplication.class,
        DaoScanMarker.class
})
@EnableScheduling
public class CandleApplication {

    public static void main(String[] args) {
        SpringApplication.run(CandleApplication.class, args);
    }

}