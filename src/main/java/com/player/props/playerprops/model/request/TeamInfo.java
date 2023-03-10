package com.player.props.playerprops.model.request;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TeamInfo {
  int id;

  String abbreviation;

  String city;

  String conference;

  String division;

  String full_name;

  String name;
}
