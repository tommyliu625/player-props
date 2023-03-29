package com.player.props.service;

import java.util.List;
import java.util.Map;

import com.player.props.dao.PlayerGameFactEntity;
import com.player.props.model.request.GenericRequestBody;
import com.player.props.model.response.SuccessfulSaveResponse;

public interface PlayerGameService {
  SuccessfulSaveResponse savePlayerGames() throws Exception;

  List<PlayerGameFactEntity> getPlayerGames(Map<String, String> params, GenericRequestBody request
  ) throws Exception;

  SuccessfulSaveResponse startJob() throws Exception;
}
