package com.player.props.model.response;

import org.springframework.lang.Nullable;

import lombok.Data;

@Data
public class SuccessfulSaveResponse {

  boolean savedSuccessfully;

  @Nullable
  Integer recordsSaved;

}
