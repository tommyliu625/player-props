package com.player.props.processor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.player.props.service.impl.PlayerPropsServiceImpl;
import com.player.props.sqlexec.SQLCommandExecutor;

import static com.player.props.constants.DbConstants.*;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class PlayerPropsProc {

  SQLCommandExecutor sqlCommandExecutor;

  @Autowired
  PlayerPropsServiceImpl playerPropsServiceImpl;

  public PlayerPropsProc(SQLCommandExecutor sqlCommandExecutor) {
    this.sqlCommandExecutor = sqlCommandExecutor;
  }

  String truncate_staging_table = String.format("truncate %1$s", PLAYER_PROPS_TABLE);
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
      "pgh.pts as pts, \n" +
      "pgh.rbs as rbs, \n" +
      "pgh.asts as asts, \n" +
      "pgh.blks as blks, \n" +
      "pgh.stls as stls, \n" +
      "pgh.tos as tos, \n" +
      "pgh.fgm as fgm, \n" +
      "pgh.fga as fga, \n" +
      "pgh.fg_pct as fg_pct, \n" +
      "pgh.fg3m as fg3m, \n" +
      "pgh.fg3a as fg3a, \n" +
      "pgh.fg3_pct as fg3_pct, \n" +
      "pgh.ftm as ftm, \n" +
      "pgh.fta as fta, \n" +
      "pgh.ft_pct as ft_pct, \n" +
      "pgh.oreb as oreb, \n" +
      "pgh.dreb as dreb, \n" +
      "pgh.pf as pf, \n" +
      "pgh.min as min, \n" +
      "CASE WHEN gh.ht_full_name = ti.full_name THEN gh.at_full_name ELSE gh.ht_full_name END AS opposing_team_full_name, \n"
      +
      "CASE WHEN gh.ht_id = ti.team_id THEN gh.at_id ELSE gh.ht_id END AS opposing_team_id, \n" +
      "pgh.postseason, \n" +
      "pgh.season \n" +
      "from %1$s as pgh left join %3$s as pi on pgh.player_id = pi.player_id left join %4$s as ti on pgh.team_id = ti.team_id \n"
      +
      "left join %5$s gh on gh.game_id = pgh.game_id", PLAYER_PROPS_TABLE,
      PLAYER_PROPS_FACT_TABLE, PLAYER_INFO_TARGET_TABLE, TEAM_INFO_TABLE, GAME_HISTORY_TARGET_FACT_TABLE);

  String truncate_player_info_distinct = String.format("truncate %1$s", PLAYER_INFO_DISTINCT_TABLE);

  String insert_distinct_player_info = String.format("insert into %1$s \n" +
      "select distinct(fact.player_id), pit.first_name, pit.last_name, pit.position,  \n" +
      "pit.team_id from %2$s fact left join  \n" + "%3$s pit on pit.player_id = fact.player_id ;",
      PLAYER_INFO_DISTINCT_TABLE, PLAYER_PROPS_FACT_TABLE, PLAYER_INFO_TARGET_TABLE);

  public boolean process() {

    try {
      boolean executeStatus;

      executeStatus = sqlCommandExecutor.execute(truncate_staging_table);
      log.info("SQL Exec Status truncating {} with status {}",
      truncate_staging_table, executeStatus);

      playerPropsServiceImpl.startJob();

      executeStatus = sqlCommandExecutor.execute(insert_to_normalize_target);
      log.info("SQL Exec Status inserting to normalized target {} with status {}", insert_to_normalize_target,
          executeStatus);

      executeStatus = sqlCommandExecutor.execute(truncate_player_info_distinct);
      log.info("SQL Exec Status truncating player_info_distinct {} with status {}", truncate_player_info_distinct,
          executeStatus);

      executeStatus = sqlCommandExecutor.execute(insert_distinct_player_info);
      log.info("SQL Exec Status inserting to distinct player info {} with status {}", insert_distinct_player_info,
          executeStatus);

      return true;
    } catch (Exception e) {
      log.error(e.getMessage());
    }
    return false;
  }

  public void normalize() {
    try {
      sqlCommandExecutor.execute(insert_to_normalize_target);
      log.info("SQL Exec Status inserting to normalized target {} with status {}", insert_to_normalize_target, true);
    } catch (Exception e) {
      log.error(e.getLocalizedMessage());
    }
  }
}
