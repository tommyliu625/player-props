package com.player.props.playerprops.controller;

import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.player.props.playerprops.config.DbConfig;

@RestController
@RequestMapping("api/v1")
public class TestPSQL {

  @Autowired
  DbConfig dbConfig;

  @GetMapping(value = "/getRecords", produces="application/json")
  public Integer getRecords() throws SQLException {
    Connection conn = dbConfig.getConnection();
    Statement stmt = conn.createStatement();
    ResultSet rs = stmt.executeQuery("SELECT * FROM public.games");

    int num = 0;
    while(rs.next()) {
      String id = rs.getString("id");
      Date date = rs.getDate("date");
      Integer ht_score = rs.getInt("home_team_score");
      num = ht_score;
    }
    return num;
  }
}
