package com.player.props.dao;

import java.sql.Date;

import javax.persistence.*;

import static com.player.props.constants.DbConstants.PLAYER_PROPS_TABLE;

import lombok.Data;

@Entity
@Table(name = PLAYER_PROPS_TABLE)
@Data
public class ANOT_NEEDED_PlayerPropsEntity {

  @Id
  @Column(name = "player_game_id")
  public String player_game_id;

  @Column(name = "player_id")
  public String player_id;

  @Column(name = "game_id")
  public String game_id;

  @Column(name = "team_id")
  public String team_id;

  @Column(name = "date")
  public Date date;

  @Column(name = "pts")
  public int pts;

  @Column(name = "rbs")
  public int rbs;

  @Column(name = "asts")
  public int asts;

  @Column(name = "fga")
  public int fga;

  @Column(name = "fgm")
  public int fgm;
  
  @Column(name = "fg_pct")
  public double fg_pct;

  @Column(name = "fg3a")
  public int fg3a;

  @Column(name = "fg3m")
  public int fg3m;

  @Column(name = "fg3_pct")
  public double fg3_pct;

  @Column(name = "fta")
  public int fta;

  @Column(name = "ftm")
  public int ftm;
  
  @Column(name = "ft_pct")
  public double ft_pct;

  @Column(name = "blks")
  public int blks;

  @Column(name = "stls")
  public int stls;

  @Column(name = "tos")
  public int tos;

  @Column(name = "postseason")
  public boolean postseason;

  @Column(name = "season")
  public int season;

}
