package com.player.props.dao;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import static com.player.props.constants.DbConstants.GAME_HISTORY_TARGET_TABLE;

import lombok.Data;

@Entity
@Table(name = GAME_HISTORY_TARGET_TABLE)
@Data
public class GamesFactEntity {

  @Id
  @Column(name = "game_id")
  private String game_id;

  @Column(name = "date")
  private String date;

  @Column(name = "ht_id")
  private String ht_id;

  @Column(name = "ht_abbreviation")
  private String ht_abbreviation;

  @Column(name = "ht_city")
  private String ht_city;

  @Column(name = "ht_conference")
  private String ht_conference;

  @Column(name = "ht_division")
  private String ht_division;

  @Column(name = "ht_full_name")
  private String ht_full_name;

  @Column(name = "ht_score")
  private String ht_score;

  @Column(name = "at_id")
  private String at_id;

  @Column(name = "at_abbreviation")
  private String at_abbreviation;

  @Column(name = "at_city")
  private String at_city;

  @Column(name = "at_conference")
  private String at_conference;

  @Column(name = "at_division")
  private String at_division;

  @Column(name = "at_full_name")
  private String at_full_name;

  @Column(name = "at_score")
  private String at_score;

  @Column(name = "postseason")
  private String postseason;

  @Column(name = "season")
  private String season;
}
