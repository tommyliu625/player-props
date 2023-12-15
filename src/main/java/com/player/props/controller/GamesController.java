package com.player.props.controller;

import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.player.props.dao.ANOT_NEEDED_GamesEntity;
import com.player.props.dao.GamesFactEntity;
import com.player.props.dao.GamesEntity;
import com.player.props.model.request.GenericRequestBody;
import com.player.props.model.response.SuccessfulSaveResponse;
import com.player.props.processor.GameProc;
import com.player.props.service.GameService;

import lombok.extern.slf4j.Slf4j;

@CrossOrigin
@RestController
@RequestMapping("/api/v1")
@Slf4j
public class GamesController {

  @Autowired
  GameService gameService;

  @Autowired
  GameProc gameProc;

  @Autowired
  EntityManagerFactory emf;

  @GetMapping(value = "/games-info", produces = "application/json")
  public List<GamesEntity> getGames(@RequestParam Map<String, String> params) throws Exception, SQLException, InterruptedException {
    EntityManager em = null;
    List<GamesEntity> result = null;
    try {
      em = emf.createEntityManager();
      CriteriaBuilder cb = em.getCriteriaBuilder();
      CriteriaQuery<GamesEntity> query = cb.createQuery(GamesEntity.class);
      Root<GamesEntity> root = query.from(GamesEntity.class);
      query.select(root);
      TypedQuery<GamesEntity> typedQuery = em.createQuery(query);
      result = typedQuery.getResultList();
    } catch (Exception e) {
      log.error(e.getMessage());
    } finally {
      if (em != null) {
        em.close();
      }
    }
    return result;
  }

  // dynamic query to get list of games
  @PostMapping(value = "/games-info-data", produces = "application/json")
  public List<GamesFactEntity> getGamesData(@RequestBody GenericRequestBody request) throws Exception {
    log.info("START: GET Games Data");
    Instant startTime = Instant.now();
    List<GamesFactEntity> result = gameService.getGamesData(request);
    Instant endTime = Instant.now();
    log.info("END: GET Games Data, timeElapsed: {}", Duration.between(startTime, endTime).toMillis());
    return result;
  }
  

  @GetMapping(value = "/games-info-normalize", produces = "application/json")
  public void normalizeGames() {
    log.info("START: Normalizing Games");
    gameProc.normalize();
    log.info("END: Normalizing Games");
  }

  // this method gets the game info in a time range up to the current date
  @PostMapping(value = "/save-game-info", produces = "application/json")
  public SuccessfulSaveResponse saveGames(@RequestParam Map<String, String> params) throws Exception {

    log.info("START: POST Save Games");
    SuccessfulSaveResponse response = gameService.saveGames(params);
    log.info("END: POST Save Games");
      return response;
  }

}
