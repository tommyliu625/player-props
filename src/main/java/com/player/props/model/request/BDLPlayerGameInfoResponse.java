package com.player.props.model.request;

import java.util.List;

import lombok.Data;

@Data
public class BDLPlayerGameInfoResponse {

  private List<BDLPlayerGameInfo> data;

  private MetaInfo meta;
}
