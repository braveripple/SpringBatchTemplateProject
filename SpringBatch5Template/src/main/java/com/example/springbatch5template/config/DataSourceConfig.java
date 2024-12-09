package com.example.springbatch5template.config;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class DataSourceConfig {

    @Bean
    DataSource mainDataSource() {
		return null;
	}

    @Bean
	PlatformTransactionManager mainTxManager() {
		return null;
	}
    
    @Bean
	JdbcClient mainJdbcClient() {
		return null;
	}

}
