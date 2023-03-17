package com.player.props.model.request;

import lombok.Data;

@Data
public class BDLPlayerGameInfo {

  private int id;

  private int ast;

  private int blk;

  private int fg3m;

  private BDLGameInfo game;

  private BDLPlayerInfo player;

  private int pts;

  private int reb;

  private int stl;

  private BDLTeamInfo team;

  private int turnover;
}
