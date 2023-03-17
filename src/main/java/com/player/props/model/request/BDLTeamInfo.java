package com.player.props.model.request;

import lombok.Data;

@Data
public class BDLTeamInfo {
  private int id;

  private String abbreviation;

  private String city;

  private String conference;

  private String division;

  private String full_name;

  private String name;
}
