package com.player.props.playerprops.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.player.props.playerprops.config.DbConfig;
import com.player.props.playerprops.model.request.BDLResponseInfo;
import com.player.props.playerprops.model.request.GameInfo;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1")
@Slf4j
public class GamesController {

  @Autowired
  DbConfig dbConfig;

  @GetMapping(value = "/getGames", produces="application/json")
  public Object getGames(@RequestParam Map<String, String> params) throws Exception, SQLException {
    String url = "https://www.balldontlie.io/api/v1/games";
    StringBuilder str = new StringBuilder();
    str.append(url).append("?");
    params.forEach((key, value) -> {
      str.append(key).append("=").append(value).append("&");
    });
    String newUrl = str.toString();
    RestTemplate restTemplate = new RestTemplate();
    ResponseEntity<BDLResponseInfo> response = restTemplate.getForEntity(newUrl, BDLResponseInfo.class);
    List<GameInfo> gameInfo = response.getBody().getData();
    // ResponseEntity<Object> response = restTemplate.getForEntity(newUrl, Object.class);
    // response.getBody().setHello("hello");

    gameInfo.forEach(game -> {
      try {
        Connection conn = dbConfig.getConnection();
        String sql = "INSERT INTO games (id,date,home_team,home_team_score,period,postseason,season,status,time,visitor_team,visitor_team_score) VALUES " +
        "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement stmt = conn.prepareStatement(sql);
        ObjectMapper mapper = new ObjectMapper();
        stmt.setString(1, game.getId());
        stmt.setDate(2, game.getDate());
        stmt.setString(3, mapper.writeValueAsString(game.getHome_team()));
        stmt.setInt(4, game.getHome_team_score());
        stmt.setInt(5, game.getPeriod());
        stmt.setBoolean(6, game.isPostseason());
        stmt.setInt(7, game.getSeason());
        stmt.setString(8, game.getStatus());
        stmt.setString(9, game.getTime());
        stmt.setString(10, mapper.writeValueAsString(game.getVisitor_team()));
        stmt.setInt(11, game.getVisitor_team_score());

      int rowInserted = stmt.executeUpdate();
      } catch (Exception  e) {
        log.error(e.getMessage());
      }
    });
    return gameInfo;
  }
}
