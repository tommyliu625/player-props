package com.player.props.service;

import java.util.List;

import com.player.props.dao.PlayerInfoDistinctEntity;
import com.player.props.model.request.GenericRequestBody;
import com.player.props.model.response.SuccessfulSaveResponse;

public interface PlayerInfoService {

  List<PlayerInfoDistinctEntity> getPlayerData(GenericRequestBody request) throws Exception;

  SuccessfulSaveResponse savePlayerInfo() throws Exception;
}
