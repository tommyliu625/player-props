package com.player.props.playerprops.model.request;

import lombok.Data;

@Data
public class TeamInfo {
  int id;

  String abbreviation;

  String city;

  String conference;

  String division;

  String full_name;

  String name;
}
