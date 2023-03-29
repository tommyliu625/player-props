package com.player.props.dao;

import javax.persistence.*;

import lombok.Data;

import static com.player.props.constants.DbConstants.TEAM_INFO_TABLE;

@Entity
@Table(name = TEAM_INFO_TABLE)
@Data
public class TeamInfoEntity {

  @Id
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

  @Column(name = "name")
  private String name;
}
