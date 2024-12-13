package com.trade.bot;

import com.dao.bot.DaoScanMarker;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackageClasses = {
		TradeBotApplication.class,
		DaoScanMarker.class
})
@EnableScheduling
public class TradeBotApplication {

	public static void main(String[] args) {
		SpringApplication.run(TradeBotApplication.class, args);
	}

}
