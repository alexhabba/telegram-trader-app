package com.trade.bot;

import com.dao.bot.DaoScanMarker;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackageClasses = {
		TradeBotApplication.class,
		DaoScanMarker.class
})
public class TradeBotApplication {

	public static void main(String[] args) {
		SpringApplication.run(TradeBotApplication.class, args);
	}

}
