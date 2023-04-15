package com.player.props.processor;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class RunProcess {

  final PlayerGameProc playerGameProc;

  final PlayerInfoProc playerInfoProc;

  final GameProc gameProc;

  RunProcess(PlayerGameProc playerGameProc, PlayerInfoProc playerInfoProc, GameProc gameProc) {
    this.playerGameProc = playerGameProc;
    this.playerInfoProc = playerInfoProc;
    this.gameProc = gameProc;
  }

  @Scheduled(cron = "30 3 7 * * ?", zone = "US/Eastern")
  public void runProcess() throws Exception {
    // TODO Auto-generated method stub
    try {
      log.info("Starting normalization process");
      if (playerInfoProc.process() != true) {
        log.error("Error with Normalization process");
        throw new Exception();
      }
      if (gameProc.process() != true) {
        log.error("Error with Normalization process");
        throw new Exception();
      }
      if (playerGameProc.process() != true) {
        log.error("Error with Normalization process");
        throw new Exception();
      }

      log.info("Ending normalization process");
    } catch (Exception e) {
      log.error("Error with Normalization process");
      log.error(e.getMessage());
    }
  }
}
