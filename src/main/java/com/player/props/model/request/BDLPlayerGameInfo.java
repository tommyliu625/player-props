package com.player.props.model.request;

import lombok.Data;

@Data
public class BDLPlayerGameInfo {

  public int id;

  public int pts;

  public int reb;
  
  public int ast;
  
  public int stl;

  public int blk;

  public int oreb;

  public int dreb;

  public int pf;

  public String min;

  public int fg3a;
  
  public int fg3m;

  public double fg3_pct;

  public double fg_pct;

  public int fga;

  public int fgm;

  public double ft_pct;

  public int fta;

  public int ftm;

  public BDLGameInfo game;

  public BDLPlayerInfo player;

  public BDLTeamInfo team;

  public int turnover;
}
