package com.player.props.model.response;

import java.sql.Date;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PlayerStatsResponse {
  public String player_game_id;
  public String game_id;
  public Date date;
  public String player_id;
  public String first_name;
  public String last_name;
  public String position;
  public String team_id;
  public String abbreviation;
  public String city;
  public String conference;
  public String division;
  public String full_name;
  public int pts;
  public int rbs;
  public int asts;
  public int stls;
  public int blks;
  public int tos;
  public int fg3m;
  public int fg3a;
  public double fg3_pct;
  public int fgm;
  public int fga;
  public double fg_pct;
  public int ftm;
  public int fta;
  public double ft_pct;
  public int oreb;
  public int dreb;
  public int pf;
  public int min;
  public String opposing_team_full_name;
  public String opposing_team_id;
  public boolean postseason;
  public int season;
}
