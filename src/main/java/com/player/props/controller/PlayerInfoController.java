package com.player.props.controller;

import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.player.props.dao.PlayerInfoDistinctEntity;
import com.player.props.dao.PlayerInfoEntity;
import com.player.props.model.request.GenericRequestBody;
import com.player.props.model.response.SuccessfulSaveResponse;
import com.player.props.service.impl.PlayerInfoServiceImpl;

import lombok.extern.slf4j.Slf4j;

@CrossOrigin
@RestController
@Slf4j
@RequestMapping("/api/v1")
public class PlayerInfoController {

  @Autowired
  EntityManagerFactory emf;

  @Autowired
  PlayerInfoServiceImpl playerInfoService;

  @GetMapping(value = "/player-info", produces = MediaType.APPLICATION_JSON_VALUE)
  public List<PlayerInfoEntity> getPlayerInfo(@RequestParam Map<String, String> params) {
    EntityManager em = emf.createEntityManager();
    em.getTransaction().begin();
    TypedQuery<PlayerInfoEntity> query = em.createQuery("SELECT e FROM PlayerInfoEntity e", PlayerInfoEntity.class);

    List<PlayerInfoEntity> result = query.getResultList();
    return result;
  }

  @PostMapping(value = "/player-info-data", produces = MediaType.APPLICATION_JSON_VALUE)
  public List<PlayerInfoDistinctEntity> getPlayerInfo(@RequestBody GenericRequestBody requestBody) {
    log.info("START: GET Player Information");
    List<PlayerInfoDistinctEntity> result = playerInfoService.getPlayerData(requestBody);
    log.info("END: GET Player Information");
    return result;
  }

  @GetMapping(value = "/start-player-info", produces = MediaType.APPLICATION_JSON_VALUE)
  public void startPlayerInfo() {
    log.info("START: Starting Cron Job For Player Info");
    // playerInfoService.startJob();
    log.info("END: Starting Cron Job For Player Info");
  }

  @PostMapping(value = "/player-info", produces = MediaType.APPLICATION_JSON_VALUE)
  public SuccessfulSaveResponse postPlayerInfo(@RequestParam Map<String, String> params) {

    log.info("START: POST Save Player Info");
    SuccessfulSaveResponse saveResponse = playerInfoService.savePlayerInfo();
    log.info("END: POST Save Player Info");
    return saveResponse;
  }
}
