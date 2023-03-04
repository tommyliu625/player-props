package com.player.props.playerprops.model.request;

import java.util.Date;

import lombok.Data;

@Data
public class GameInfo {
  String id;

  Date date;

  TeamInfo home_team;

  Integer home_team_score;

  Integer period;

  boolean postseason;

  Integer season;

  String status;

  String time;

  TeamInfo visitor_team;

  Integer visitor_team_score;
}
