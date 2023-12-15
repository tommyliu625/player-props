package com.player.props.dao;

import java.io.Serializable;
import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import static com.player.props.constants.DbConstants.PLAYER_PROPS_FACT_TABLE;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = PLAYER_PROPS_FACT_TABLE)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlayerPropsFactEntity implements Serializable {

  @Id
  @Column(name = "player_game_id")
  public String player_game_id;

  @Column(name = "game_id")
  public String game_id;

  @Column(name = "date")
  public Date date;

  // player information
  @Column(name = "player_id")
  public String player_id;

  public FullName fullName;

  @Column(name = "position")
  public String position;

  // team information
  @Column(name = "team_id")
  public String team_id;

  @Column(name = "abbreviation")
  public String abbreviation;

  @Column(name = "city")
  public String city;

  @Column(name = "conference")
  public String conference;

  @Column(name = "division")
  public String division;

  @Column(name = "full_name")
  public String full_name;

  @Column(name = "pts")
  public int pts;

  @Column(name = "rbs")
  public int rbs;

  @Column(name = "asts")
  public int asts;

  @Column(name = "stls")
  public int stls;

  @Column(name = "blks")
  public int blks;

  @Column(name = "tos")
  public int tos;

  @Column(name = "fg3m")
  public int fg3m;

  @Column(name = "fg3a")
  public int fg3a;

  @Column(name = "fg3_pct")
  public double fg3_pct;

  @Column(name = "fgm")
  public int fgm;

  @Column(name = "fga")
  public int fga;

  @Column(name = "fg_pct")
  public double fg_pct;

  @Column(name = "ftm")
  public int ftm;

  @Column(name = "fta")
  public int fta;

  @Column(name = "ft_pct")
  public double ft_pct;

  @Column(name = "opposing_team_full_name")
  public String opposing_team_full_name;

  @Column(name = "opposing_team_id")
  public String opposing_team_id;

  @Column(name = "postseason")
  public boolean postseason;

  @Column(name = "season")
  public int season;

  @Column(name = "oreb")
  public int oreb;

  @Column(name = "dreb")
  public int dreb;

   @Column(name = "pf")
  public int pf;

  @Column(name = "min")
  public int min;

}
