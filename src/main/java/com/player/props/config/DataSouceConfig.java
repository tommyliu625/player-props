package com.player.props.config;

import static com.player.props.constants.DbConstants.DB_JDBC;
import static com.player.props.constants.DbConstants.DB_PW;
import static com.player.props.constants.DbConstants.DB_USER;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;

@Slf4j
@Configuration
public class DataSouceConfig {

  @Value("${postgres.properties.url}")
  private String url;

  @Value("${postgres.properties.user}")
  private String user;

  @Value("${postgres.properties.pw}")
  private String pw;

  @Value("${postgres.properties.driver}")
  private String driver;

  @Bean
  public HikariDataSource createDataSource() {
    log.info("Fetching DB properties");
    log.info("Url: {} | User: {} | Pw: {} | Driver: {}", url, user, pw, driver);
    final HikariConfig dataSourceProperties = new HikariConfig();
    dataSourceProperties.setJdbcUrl(url);
    dataSourceProperties.setDriverClassName(driver);
    dataSourceProperties.setUsername(user);
    dataSourceProperties.setPassword(pw);
    HikariDataSource dataSource = new HikariDataSource(dataSourceProperties);
    return dataSource;
  }
}
