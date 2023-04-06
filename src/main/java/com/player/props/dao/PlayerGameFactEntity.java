package com.player.props.dao;

import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import static com.player.props.constants.DbConstants.PLAYER_GAME_HISTORY_TARGET_TABLE;

import lombok.Data;

@Entity
@Table(name = PLAYER_GAME_HISTORY_TARGET_TABLE)
@Data
public class PlayerGameFactEntity {


  @Id
  @Column(name = "player_game_id")
  private String player_game_id;

  @Column(name = "game_id")
  private String game_id;

  @Column(name = "date")
  private Date date;

  // player information
  @Column(name = "player_id")
  private String player_id;

  @Column(name = "first_name")
  private String first_name;

  @Column(name = "last_name")
  private String last_name;

  @Column(name = "position")
  private String position;

  // team information
  @Column(name = "team_id")
  private String team_id;

  @Column(name = "abbreviation")
  private String abbreviation;

  @Column(name = "city")
  private String city;

  @Column(name = "conference")
  private String conference;

  @Column(name = "division")
  private String division;

  @Column(name = "full_name")
  private String full_name;

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

  @Column(name = "opposing_team_full_name")
  private String opposing_team_full_name;

  @Column(name = "opposing_team_id")
  private String opposing_team_id;

  @Column(name = "postseason")
  private boolean postseason;

  @Column(name = "season")
  private int season;
}
