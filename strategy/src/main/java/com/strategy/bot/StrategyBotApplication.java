package com.strategy.bot;

import com.dao.bot.DaoScanMarker;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackageClasses = {
		StrategyBotApplication.class,
		DaoScanMarker.class
})
@EnableScheduling
public class StrategyBotApplication {

	public static void main(String[] args) {
		SpringApplication.run(StrategyBotApplication.class, args);
	}

}
