package com.player.props.sqlexec;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class SQLCommandExecutor {

  DataSource dataSource;

  public SQLCommandExecutor(DataSource dataSource) {
    this.dataSource = dataSource;

  }

  public boolean execute(String sql) throws SQLException {
    boolean retry = true;
    boolean returnVal = false;
    int count = 0;
    while (retry && count < 2) {
      try (Connection connection = dataSource.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
        returnVal = preparedStatement.execute();
        retry = false;
      } catch (Exception e) {
        log.error("Error Executing Query : {} | Error: {}", sql, e);
        count += 1;
      }
    }
    return returnVal;
  }

  public boolean call(String sql, String table) throws SQLException {
    boolean retry = true;
    boolean returnValue = false;
    int count = 0;
    while (retry && count < 2) {
      try (Connection connection = dataSource.getConnection()) {
        try (CallableStatement callableStatement = connection.prepareCall(sql)) {
          // if (table != null) {
          //   callableStatement.setString(1, table);
          // }
          returnValue = callableStatement.execute();
          retry = false;

          log.info("SQL Exec Status : {}", sql);
      }
    } catch (Exception e) {
        log.error(e.getMessage());
        count += 1;
      }
    }
    return returnValue;
  }
}
