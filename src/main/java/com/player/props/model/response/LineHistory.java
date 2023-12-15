package com.player.props.model.response;

import java.sql.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public
class LineHistory {
  @JsonFormat(pattern = "yyyy-MM-dd")
  public Date date;
  public Double value;
  public Boolean hit;

}