package com.dao.bot.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories("com.dao.bot.repository")
@EntityScan("com.dao.bot.entity")
public class AppEntityConfig {}
