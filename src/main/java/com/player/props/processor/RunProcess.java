package com.player.props.processor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class RunProcess {

  final PlayerGameProc playerGameProc;

  final PlayerInfoProc playerInfoProc;

  final PlayerPropsProc playerPropsProc;

  final GameProc gameProc;

  @Autowired
  CacheManager cacheManager;

  RunProcess(PlayerGameProc playerGameProc, PlayerInfoProc playerInfoProc, GameProc gameProc, PlayerPropsProc playerPropsProc) {
    this.playerGameProc = playerGameProc;
    this.playerInfoProc = playerInfoProc;
    this.gameProc = gameProc;
    this.playerPropsProc = playerPropsProc;
  }

  @Scheduled(cron = "0 30 1 * * ?", zone = "US/Eastern")
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
      if (playerPropsProc.process() != true) {
        log.error("Error with Normalization process");
        throw new Exception();
      }
      evictAllCaches();
      log.info("Ending normalization process");
    } catch (Exception e) {
      log.error("Error with Normalization process");
      log.error(e.getMessage());
    }
  }

  public void evictAllCaches() {
    cacheManager.getCacheNames().stream()
        .forEach(cacheName -> cacheManager.getCache(cacheName).clear());
  }
}
