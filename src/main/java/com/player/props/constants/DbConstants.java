package com.player.props.constants;

public class DbConstants {

  public static String DB_URL = "jdbc:postgresql://localhost:5432/tommyliu";
  public static String DB_USER = "tommyliu";
  public static String DB_PW = "";
  public static String DB_JDBC = "org.postgresql.Driver";

  public static final String GAME_HISTORY_STAGING_TABLE = "game_history_staging";
  public static final String GAME_HISTORY_TARGET_TABLE = "game_history_target_fact";

  public static final String PLAYER_GAME_HISTORY_STAGING_TABLE = "player_game_history_staging";
  public static final String PLAYER_GAME_HISTORY_TARGET_TABLE = "player_game_history_target_fact";

  public static final String TEAM_INFO_TABLE = "team_info";

  public static final String PLAYER_INFO_STAGING_TABLE = "player_info_staging";
  public static final String PLAYER_INFO_TARGET_TABLE = "player_info_target";

}
