package com.player.props.controller;

import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.player.props.dao.PlayerGameEntity;
import com.player.props.model.response.SuccessfulSaveResponse;
import com.player.props.service.PlayerGameService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1")
@Slf4j
public class PlayerGameController {

  @Autowired
  EntityManagerFactory emf;

  @Autowired
  PlayerGameService playerGameService;

  @GetMapping(value = "/player-game-info", produces = MediaType.APPLICATION_JSON_VALUE)
  public List<PlayerGameEntity> getPlayerGameInfo(@RequestParam Map<String, String> params) {
    EntityManager em = null;
    List<PlayerGameEntity> result = null;
    try {
      em = emf.createEntityManager();
      em.getTransaction().begin();
      TypedQuery<PlayerGameEntity> query =  em.createQuery("SELECT e FROM PlayerGameEntity e", PlayerGameEntity.class);
      if (query.getResultList() != null) {
        result = query.getResultList();
      }
    } catch (Exception e) {
      log.error(e.getMessage());
    }
    return result;
  }

  @PostMapping(value = "/start-player-game-job", produces = MediaType.APPLICATION_JSON_VALUE)
  public SuccessfulSaveResponse startJob() throws Exception {
    log.info("START: Starting Cron Job For Player Game Info");
    SuccessfulSaveResponse saveResponse = playerGameService.startJob();
    log.info("START: Starting Cron Job For Player Game Info");
    return saveResponse;
  }

  @PostMapping(value = "/player-game-info", produces = MediaType.APPLICATION_JSON_VALUE)
  public SuccessfulSaveResponse postPlayerGameInfo(@RequestParam Map<String, String> params) throws Exception {

    log.info("START: POST Save Player Game");
    SuccessfulSaveResponse saveResponse = playerGameService.savePlayerGames();
    log.info("END: POST Save Player Game");
    return saveResponse;
  }
}
