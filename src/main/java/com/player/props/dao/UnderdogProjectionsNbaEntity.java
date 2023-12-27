package com.player.props.dao;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Entity
@Table(name = "underdog_projections_nba")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UnderdogProjectionsNbaEntity {

  @Id
  @Column(name = "proj_id")
  public String projId;

  public FullName fullName;

  @Column(name = "stat_type")
  public String statType;

  @Column(name = "line_score")
  public double lineScore;

  @Column(name = "payout_multiplier")
  public double payoutMultiplier;

  @Column(name = "opposing_team")
  public String opposingTeam;

  @Column(name = "game_date")
  public Date gameDate;
}
