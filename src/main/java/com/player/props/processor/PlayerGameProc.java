package com.player.props.processor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.player.props.service.impl.PlayerGameServiceImpl;
import com.player.props.sqlexec.SQLCommandExecutor;

import static com.player.props.constants.DbConstants.*;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class PlayerGameProc {

  SQLCommandExecutor sqlCommandExecutor;

  @Autowired
  PlayerGameServiceImpl playerGameServiceImpl;

  public PlayerGameProc(SQLCommandExecutor sqlCommandExecutor) {
    this.sqlCommandExecutor = sqlCommandExecutor;
  }

  public boolean process() {

    String truncate_staging_table = String.format("truncate %1$s", PLAYER_GAME_HISTORY_STAGING_TABLE);
    String insert_to_normalize_target = String.format("INSERT INTO %2$s select \n" +
      "pgh.player_game_id, \n" +
      "pgh.game_id, \n" +
      "pgh.date, \n" +
      "pgh.player_id, \n" +
      "pi.first_name, \n" +
      "pi.last_name, \n" +
      "pi.position, \n" +
      "pgh.team_id, \n" +
      "ti.abbreviation, \n" +
      "ti.city, \n" +
      "ti.conference, \n" +
      "ti.division, \n" +
      "ti.full_name, \n" +
      "pgh.pts, \n" +
      "pgh.rbs, \n" +
      "pgh.asts, \n" +
      "pgh.fg3m, \n" + 
      "pgh.blks, \n" +
      "pgh.stls, \n" +
      "pgh.tos, \n" +
      "pgh.postseason, \n" +
      "pgh.season \n" +
    "from %1$s as pgh left join %3$s as pi on pgh.player_id = pi.player_id left join %4$s as ti on pgh.team_id = ti.team_id;", PLAYER_GAME_HISTORY_STAGING_TABLE, PLAYER_GAME_HISTORY_TARGET_TABLE, PLAYER_INFO_TARGET_TABLE ,TEAM_INFO_TABLE);
    try {
      boolean executeStatus;

      executeStatus = sqlCommandExecutor.execute(truncate_staging_table);
      log.info("SQL Exec Status truncating {} with status {}", truncate_staging_table, executeStatus);

      playerGameServiceImpl.startJob();

      executeStatus = sqlCommandExecutor.execute(insert_to_normalize_target);
      log.info("SQL Exec Status inserting to normalized target {} with status {}", insert_to_normalize_target, executeStatus);

      return true;
    } catch (Exception e) {
      log.error(e.getMessage());
    }
    return false;
  }
}
