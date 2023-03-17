package com.player.props.dao;

import java.sql.Date;

import javax.persistence.*;

import lombok.Data;

@Entity
@Table(name = "player_game_history")
@Data
public class PlayerGameEntity {

  @Id
  @Column(name = "player_game_id")
  private String player_game_id;

  @Column(name = "player_id")
  private String player_id;

  @Column(name = "game_id")
  private String game_id;

  @Column(name = "team_id")
  private String team_id;

  @Column(name = "date")
  private Date date;

  @Column(name = "pts")
  private int pts;

  @Column(name = "rbs")
  private int rbs;

  @Column(name = "asts")
  private int asts;

  @Column(name = "fg3m")
  private int fg3m;

  @Column(name = "blks")
  private int blks;

  @Column(name = "stls")
  private int stls;

  @Column(name = "tos")
  private int tos;

  @Column(name = "postseason")
  private boolean postseason;

  @Column(name = "season")
  private int season;

}
