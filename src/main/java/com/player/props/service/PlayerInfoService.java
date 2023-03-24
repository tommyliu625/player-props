package com.player.props.service;

import com.player.props.model.response.SuccessfulSaveResponse;

public interface PlayerInfoService {
  SuccessfulSaveResponse savePlayerInfo() throws Exception;

  void startJob() throws Exception;
}
