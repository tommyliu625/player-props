package com.player.props.model.request;

import lombok.Data;

@Data
public class BDLPlayerInfo {

  private int id;

  private String first_name;

  private String last_name;

  private String position;

  private BDLTeamInfo team;
}
