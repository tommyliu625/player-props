package com.player.props.model.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BDLGameInfo {
  String id;

  String date;

  BDLTeamInfo home_team;

  int home_team_score;

  Integer period;

  boolean postseason;

  int season;

  String status;

  String time;

  BDLTeamInfo visitor_team;

  Integer visitor_team_score;
}
