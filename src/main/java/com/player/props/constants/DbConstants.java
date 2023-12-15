package com.player.props.constants;

public class DbConstants {

  public static String DB_URL = "jdbc:postgresql://player-props-db.cmmimhasgknf.us-east-1.rds.amazonaws.com:5432/playerprops?useSSL=false";
  // public static String DB_URL = "jdbc:postgresql://localhost/playerprops";
  public static String DB_USER = "postgres";
  // public static String DB_USER = "postgres";
  public static String DB_PW = "whothem4n";
  // public static String DB_PW = "";
  public static String DB_JDBC = "org.postgresql.Driver";

  public static final String GAME_HISTORY_STAGING_TABLE_NOT_NEED = "game_history_staging";
  public static final String GAME_HISTORY_STAGING_TABLE = "game_history_staging";
  public static final String GAME_HISTORY_TARGET_FACT_TABLE_NOT_NEED = "game_history_target_fact";
  public static final String GAME_HISTORY_TARGET_FACT_TABLE = "game_history_target_fact";

  // these need to be depracted, changing to player_props instead
  public static final String PLAYER_GAME_HISTORY_STAGING_TABLE = "player_game_history_staging";
  public static final String PLAYER_GAME_HISTORY_TARGET_TABLE = "player_game_history_target_fact";

  public static final String PLAYER_PROPS_TABLE_NOT_NEED = "player_props";
  public static final String PLAYER_PROPS_TABLE = "player_props";
  public static final String PLAYER_PROPS_FACT_TABLE_NOT_NEED = "player_props_fact";
  public static final String PLAYER_PROPS_FACT_TABLE = "player_props_fact";

  public static final String TEAM_INFO_TABLE = "team_info";

  public static final String PLAYER_INFO_STAGING_TABLE = "player_info_staging";
  public static final String PLAYER_INFO_TARGET_TABLE = "player_info_target";
  public static final String PLAYER_INFO_DISTINCT_TABLE = "player_info_distinct";

  public static final String PRIZE_PICKS_PROJECTIONS_NBA_TABLE = "prize_picks_projections_nba";

}
