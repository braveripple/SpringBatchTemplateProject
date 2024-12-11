package com.example.springbatch5template.config.datasource;

import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.batch.BatchDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import com.example.springbatch5template.component.PGCopyClient;
import com.zaxxer.hikari.HikariDataSource;

@Configuration
public class MainDataSourceConfig {

    @Bean
    @Primary
    @ConfigurationProperties(prefix = "spring.datasource.main")
    DataSource mainDataSource() {
		return (HikariDataSource) DataSourceBuilder.create().build();
	}

    @Bean
    @Primary
	PlatformTransactionManager mainTxManager() {
		return new DataSourceTransactionManager(mainDataSource());
	}
    
    @Bean
    @Primary
	JdbcClient mainJdbcClient() {
		return JdbcClient.create(mainDataSource());
	}
    
    @Bean
    @Primary
    PGCopyClient mainPGCopyClient() {
    	return new PGCopyClient(mainDataSource());
    }
    
    @Bean
    @BatchDataSource
    @ConfigurationProperties(prefix = "spring.datasource.jobrepository")
    DataSource jobRepositoryDataSource() {
    	return (HikariDataSource) DataSourceBuilder.create().build();
    }

}
