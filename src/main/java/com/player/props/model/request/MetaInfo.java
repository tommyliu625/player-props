package com.player.props.model.request;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MetaInfo {
  Integer total_pages;

  Integer current_page;

  Integer next_page;

  Integer per_page;

  Integer total_count;

}
