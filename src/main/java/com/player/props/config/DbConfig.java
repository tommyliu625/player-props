package com.player.props.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.springframework.context.annotation.Configuration;

@Configuration
public class DbConfig {

  public Connection getConnection() throws SQLException {
    String url = "jdbc:postgresql://localhost/tommyliu";
    String user = "tommyliu";
    String password = "";
    Connection conn = DriverManager.getConnection(url, user, password);
    return conn;
  }
}
