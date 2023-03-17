package com.player.props.dao;

import javax.persistence.*;

import lombok.Data;

@Entity
@Table(name = "player_info")
@Data
public class PlayerInfoEntity {

  @Id
  @Column(name = "player_id")
  private String player_id;

  @Column(name = "first_name")
  private String first_name;

  @Column(name = "last_name")
  private String last_name;

  @Column(name = "position")
  private String position;

  @Column(name = "team_id")
  private String team_id;
}
