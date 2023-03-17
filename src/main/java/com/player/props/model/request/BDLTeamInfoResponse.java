package com.player.props.model.request;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BDLTeamInfoResponse {

  private List<BDLTeamInfo> data;

  private MetaInfo meta;

}
