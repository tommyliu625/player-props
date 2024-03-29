package com.player.props.model.response;

import java.util.List;

import com.player.props.model.request.Message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatResponse {
  private List<Choice> choices;

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  public static class Choice {

    private int index;
    private Message message;

  }
}
