package com.player.props.model.request;

import java.util.Map;

import lombok.Data;

@Data
public class GenericRequestBody {

  Map<String, Map<String, Object>> where;

  Map<String, String> orderBy;

  int limit;

  String start_date;

  String end_date;

}
