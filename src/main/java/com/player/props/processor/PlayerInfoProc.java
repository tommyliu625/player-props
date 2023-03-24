package com.player.props.processor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.player.props.service.PlayerInfoService;
import com.player.props.service.impl.PlayerInfoServiceImpl;
import com.player.props.sqlexec.SQLCommandExecutor;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class PlayerInfoProc {

  SQLCommandExecutor sqlCommandExecutor;

  @Autowired
  PlayerInfoServiceImpl playerInfoService;

  public PlayerInfoProc(SQLCommandExecutor sqlCommandExecutor) {
    this.sqlCommandExecutor = sqlCommandExecutor;
  }

  public boolean process() {

    String target_table = "player_info_staging_1";
    String staging_table = "tommyliu.player_info_staging";
    String truncate_table = "truncate table player_info_staging";
    String delete_duplicate = "delete from player_info_staging using (select player_id from player_info_staging_1) as duplicates where player_info_staging.player_id = duplicates.player_id";
    String insert_target = "insert into player_info_staging_1 select * from player_info_staging";


    try {
      boolean executeStatus;

      executeStatus = sqlCommandExecutor.execute(truncate_table);
      log.info("SQL Exec Status truncating {} with status {}", staging_table, executeStatus);

      playerInfoService.savePlayerInfo();

      executeStatus = sqlCommandExecutor.execute(delete_duplicate);
      log.info("SQL Exec Status deleting duplications with status {}", staging_table, executeStatus);

      executeStatus = sqlCommandExecutor.execute(insert_target);
      log.info("SQL Exec Status inserting delta with status {}", staging_table, executeStatus);

      return true;
    } catch (Exception e) {
      log.error(e.getMessage());
    }
    return false;
  }
}
