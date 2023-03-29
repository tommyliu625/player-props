package com.player.props.processor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.player.props.service.PlayerInfoService;
import com.player.props.service.impl.PlayerInfoServiceImpl;
import com.player.props.sqlexec.SQLCommandExecutor;
import static com.player.props.constants.DbConstants.*;

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

    String truncate_table = String.format("truncate table %1$s", PLAYER_INFO_STAGING_TABLE);
    String delete_duplicate = String.format("delete from %1$s using (select player_id from %2$s) as duplicates where %1$s.player_id = duplicates.player_id", PLAYER_INFO_STAGING_TABLE, PLAYER_INFO_TARGET_TABLE);
    String insert_target = String.format("insert into %2$s select * from %1$s", PLAYER_INFO_STAGING_TABLE, PLAYER_INFO_TARGET_TABLE);


    try {
      boolean executeStatus;

      executeStatus = sqlCommandExecutor.execute(truncate_table);
      log.info("SQL Exec Status truncating {} with status {}", PLAYER_INFO_STAGING_TABLE, executeStatus);

      playerInfoService.savePlayerInfo();

      executeStatus = sqlCommandExecutor.execute(delete_duplicate);
      log.info("SQL Exec Status deleting duplications from {} with status {}", PLAYER_INFO_STAGING_TABLE, executeStatus);

      executeStatus = sqlCommandExecutor.execute(insert_target);
      log.info("SQL Exec Status inserting delta to {} with status {}", PLAYER_INFO_TARGET_TABLE, executeStatus);

      return true;
    } catch (Exception e) {
      log.error(e.getMessage());
    }
    return false;
  }
}
