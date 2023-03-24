package com.player.props.service;

import com.player.props.model.response.SuccessfulSaveResponse;

public interface PlayerGameService {
  SuccessfulSaveResponse savePlayerGames() throws Exception;

  SuccessfulSaveResponse startJob() throws Exception;
}
