package com.player.props.dao;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

import static com.player.props.constants.DbConstants.PRIZE_PICKS_PROJECTIONS_NBA_TABLE;

import java.io.Serializable;
import java.sql.Date;

@Data
@Entity
@Table(name = PRIZE_PICKS_PROJECTIONS_NBA_TABLE)
public class PrizePicksProjectionsNbaEntity implements Serializable {  
  
    @Id
    @Column(name = "proj_id", nullable = false)
    public Long id;

    public FullName fullName;

    @Column(name = "line_score", nullable = false)
    public double lineScore;

    @Column(name = "stat_type", nullable = false)
    public String statType;

    @Column(name = "opposing_team", nullable = false)
    public String opposingTeam;

    @Column(name = "game_date", nullable = false)
    public Date gameDate;
}
