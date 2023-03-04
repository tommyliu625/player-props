package com.player.props.playerprops.model.request;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BDLResponseInfo {
  String hello;

  List<GameInfo> data;

  MetaInfo meta;

  @Data
  @JsonInclude(JsonInclude.Include.NON_NULL)
  public class MetaInfo {
    Integer total_pages;

    Integer current_page;

    Integer next_page;

    Integer per_page;

    Integer total_count;

  }
}
