package com.signal.bot;

import com.dao.bot.DaoScanMarker;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackageClasses = {
        SignalBotApplication.class,
        DaoScanMarker.class
})
@EnableScheduling
public class SignalBotApplication {

    public static void main(String[] args) {
        SpringApplication.run(SignalBotApplication.class, args);
    }

}
