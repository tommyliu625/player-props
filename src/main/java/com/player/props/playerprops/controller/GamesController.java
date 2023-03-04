package com.player.props.playerprops.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.player.props.playerprops.model.request.BDLResponseInfo;

@RestController
@RequestMapping("/api/v1")
public class GamesController {

  @GetMapping(value = "/getGames", produces="application/json")
  public Object getGames(@RequestParam Map<String, String> params) {
    String url = "https://www.balldontlie.io/api/v1/games";
    StringBuilder str = new StringBuilder();
    str.append(url).append("?");
    params.forEach((key, value) -> {
      str.append(key).append("=").append(value).append("&");
    });
    String newUrl = str.toString();
    RestTemplate restTemplate = new RestTemplate();
    ResponseEntity<BDLResponseInfo> response = restTemplate.getForEntity(newUrl, BDLResponseInfo.class);

    return response.getBody();
  }
}
