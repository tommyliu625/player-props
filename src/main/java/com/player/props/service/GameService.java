package com.player.props.service;

import java.util.List;
import java.util.Map;

import com.player.props.dao.GamesFactEntity;
import com.player.props.model.request.GenericRequestBody;
import com.player.props.model.response.SuccessfulSaveResponse;

public interface GameService {

  SuccessfulSaveResponse saveGames(Map<String, String> params) throws Exception;

  SuccessfulSaveResponse startJob() throws Exception;

  List<GamesFactEntity> getGamesData(GenericRequestBody request);
}
