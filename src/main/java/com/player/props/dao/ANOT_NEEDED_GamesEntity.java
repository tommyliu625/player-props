package com.player.props.dao;

import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

import static com.player.props.constants.DbConstants.GAME_HISTORY_STAGING_TABLE;

@Entity
@Table(name = GAME_HISTORY_STAGING_TABLE)
@Data
public class ANOT_NEEDED_GamesEntity {

  @Id
  @Column(name = "game_id")
  private String game_id;

  @Column(name = "date")
  private Date date;

  @Column(name = "ht_id")
  private String ht_id;

  @Column(name = "ht_score")
  private int ht_score;

  @Column(name = "at_id")
  private String at_id;

  @Column(name = "at_score")
  private int at_score;

  @Column(name = "postseason")
  private boolean postseason;

  @Column(name = "season")
  private int season;

}
