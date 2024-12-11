package com.example.springbatch5template.config.datasource;

import javax.sql.DataSource;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import com.example.springbatch5template.component.PGCopyClient;
import com.zaxxer.hikari.HikariDataSource;

@Configuration
public class SubDataSourceConfig {
	
    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.sub")
    DataSource subDataSource() {
		return (HikariDataSource) DataSourceBuilder.create().build();
	}

    @Bean
    PlatformTransactionManager subTxManager() {
		return new DataSourceTransactionManager(subDataSource());
	}
    
    @Bean
    JdbcClient subJdbcClient() {
		return JdbcClient.create(subDataSource());
	}
    
    @Bean
    PGCopyClient subPGCopyClient() {
    	return new PGCopyClient(subDataSource());
    }

}
