package com.player.props.controller;

import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import javax.persistence.Entity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.player.props.config.DbConfig;
import com.player.props.processor.GameProc;
import com.player.props.processor.PlayerGameProc;
import com.player.props.processor.PlayerInfoProc;
import com.player.props.processor.RunProcess;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("api/v1")
public class SQLExecutorController {

  @Autowired
  DbConfig dbConfig;

  @Autowired
  PlayerInfoProc playerInfoProc;

  @Autowired
  PlayerGameProc playerGameProc;

  @Autowired
  GameProc gameProc;

  @Autowired
  RunProcess runProcess;

  // @GetMapping(value = "/getRecords", produces = "application/json")
  // public Integer getRecords() throws SQLException {
  //   Connection conn = dbConfig.getConnection();
  //   Statement stmt = conn.createStatement();
  //   ResultSet rs = stmt.executeQuery("SELECT * FROM public.games");

  //   int num = 0;
  //   while (rs.next()) {
  //     String id = rs.getString("id");
  //     Date date = rs.getDate("date");
  //     Integer ht_score = rs.getInt("home_team_score");
  //     num = ht_score;
  //   }
  //   return num;
  // }

  @GetMapping(value = "/player-info-sql-exec", produces = MediaType.APPLICATION_JSON_VALUE)
  public boolean execSql() {
    log.info("Starting Player Info Processor");
    boolean res = playerInfoProc.process();
    log.info("Ending Player Info Processor");
    return res;
  }

  @GetMapping(value = "/player-game-sql-exec", produces = MediaType.
  APPLICATION_JSON_VALUE)
  public boolean execSqlPlayerGame() {
    log.info("Starting Player Info Processor");
    boolean res = playerGameProc.process();
    log.info("Ending Player Info Processor");
    return res;
  }

  @GetMapping(value = "/game-sql-exec", produces = MediaType.APPLICATION_JSON_VALUE)
  public boolean execSqlGame() {
    log.info("Starting Game Processor");
    boolean res = gameProc.process();
    log.info("Ending Game Processor");
    return res;
  }

  @GetMapping(value = "/run-process", produces = MediaType.APPLICATION_JSON_VALUE)
  public void runProcessExec() throws Exception {
    log.info("Starting All Processors");
    runProcess.runProcess();
    log.info("Ending All Processors");
  }
}
