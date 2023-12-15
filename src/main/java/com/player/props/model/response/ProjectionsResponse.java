package com.player.props.model.response;

import java.util.List;
import java.util.Map;

import com.player.props.dao.PlayerPropsFactEntity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProjectionsResponse {

  public Map<String, List<PlayerProjectionInformation>> stat_projections;
  
  public Map<String, List<PlayerPropsFactEntity>> player_history;
}
