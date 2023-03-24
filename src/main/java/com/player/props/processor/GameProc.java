package com.player.props.processor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.player.props.service.GameService;
import com.player.props.sqlexec.SQLCommandExecutor;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class GameProc {

  SQLCommandExecutor sqlCommandExecutor;

  GameService gameService;

  GameProc(SQLCommandExecutor sqlCommandExecutor, GameService gameService) {
    this.sqlCommandExecutor = sqlCommandExecutor;
    this.gameService = gameService;
  }

  public boolean process() {

    String truncate_staging_table = "truncate game_history_staging";
    String insert_to_fact_target = "INSERT INTO game_history_target_fact select \n " +
      "gh.game_id, \n" +
      "gh.date, \n" +
      "ht.abbreviation as ht_abbreviation, \n" +
      "ht.city as ht_city, \n" +
      "ht.conference as ht_conference, \n" +
      "ht.division as ht_division, \n" +
      "ht.full_name as ht_full_name, \n" +
      "gh.ht_score, \n" +
      "awayt.abbreviation as at_abbreviation, \n" +
      "awayt.city as at_city, \n" +
      "awayt.conference as at_conference, \n" +
      "awayt.division as at_division, \n" +
      "awayt.full_name as at_full_name, \n" +
      "gh.at_score, \n" +
      "gh.postseason, \n" +
      "gh.season \n" +
    "from game_history_staging gh left join team_info ht on gh.ht_id = ht.team_id left join team_info awayt on gh.at_id = awayt.team_id;";

    try {
      boolean executeStatus;

      executeStatus = sqlCommandExecutor.execute(truncate_staging_table);
      log.info("SQL Exec Status truncating {} with status {}", truncate_staging_table, executeStatus);

      gameService.startJob();

      executeStatus = sqlCommandExecutor.execute(insert_to_fact_target);
      log.info("SQL Exec Status inserting to normalized target {} with status {}", insert_to_fact_target, executeStatus);

      return true;
    } catch (Exception e) {
      log.error(e.getMessage());
    }
    return false;
  }
}
