package com.player.props.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

@Configuration
public class DataSouceConfig {

  @Bean
  public HikariDataSource createDataSource() {
    final HikariConfig dataSourceProperties = new HikariConfig();
    dataSourceProperties.setJdbcUrl("jdbc:postgresql://localhost/tommyliu");
    dataSourceProperties.setDriverClassName("org.postgresql.Driver");
    HikariDataSource dataSource = new HikariDataSource(dataSourceProperties);
    return dataSource;
  }
}
