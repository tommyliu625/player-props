package com.player.props.controller;

import java.sql.Date;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.player.props.dao.GamesEntity;
import com.player.props.dao.GamesFactEntity;
import com.player.props.model.request.BDLGameInfo;
import com.player.props.model.request.BDLGameInfoResponse;
import com.player.props.model.request.GenericRequestBody;
import com.player.props.model.response.SuccessfulSaveResponse;
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
  EntityManagerFactory emf;

  @GetMapping(value = "/games-info", produces = "application/json")
  public List<GamesEntity> getGames(@RequestParam Map<String, String> params) throws Exception, SQLException, InterruptedException {
    EntityManager em = null;
    List<GamesEntity> result = null;
    try {
      em = emf.createEntityManager();
      em.getTransaction().begin();
      TypedQuery<GamesEntity> query = em.createQuery("SELECT g FROM GamesEntity g", GamesEntity.class);

      if (query.getResultList() != null) {
        result = query.getResultList();
      }
    } catch (Exception e) {
      log.error(e.getMessage());
    } finally {
      if (em != null) {
        em.close();
      }
    }
    return result;
  }

  @PostMapping(value = "/games-info-data", produces = "application/json")
  public List<GamesFactEntity> getGamesData(@RequestBody GenericRequestBody request) throws Exception {
    log.info("START: GET Games Data");
    List<GamesFactEntity> result = gameService.getGamesData(request);
    log.info("END: GET Games Data");
    return result;
  }

  @PostMapping(value = "/games-info", produces = "application/json")
  public SuccessfulSaveResponse saveGames(@RequestParam Map<String, String> params) throws Exception {

    log.info("START: POST Save Games");
    SuccessfulSaveResponse response = gameService.saveGames();
    log.info("END: POST Save Games");
      return response;
  }

}
