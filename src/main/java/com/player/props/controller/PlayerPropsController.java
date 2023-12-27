package com.player.props.controller;

import java.time.Duration;
import java.time.Instant;
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

import com.player.props.dao.PlayerPropsEntity;
import com.player.props.model.request.GenericRequestBody;
import com.player.props.model.response.LineHistory;
import com.player.props.model.response.PlayerStatsResponse;
import com.player.props.model.response.ProjectionsResponse;
import com.player.props.model.response.SuccessfulSaveResponse;
import com.player.props.processor.PlayerPropsProc;
import com.player.props.service.impl.PrizePicksProjectionsServiceImpl;
import com.player.props.service.impl.UnderdogProjectionsServiceImpl;
import com.player.props.util.DateUtil;
import com.player.props.service.impl.PlayerPropsServiceImpl;

import lombok.extern.slf4j.Slf4j;

@CrossOrigin
@RestController
@RequestMapping("/api/v1")
@Slf4j
public class PlayerPropsController {

  @Autowired
  EntityManagerFactory emf;

  @Autowired
  PlayerPropsServiceImpl playerPropsService;

  @Autowired
  PlayerPropsProc playerPropsProc;

  @Autowired
  PrizePicksProjectionsServiceImpl picksProjectionsService;

  @Autowired
  UnderdogProjectionsServiceImpl underdogProjectionsServiceImpl;

  @GetMapping(value = "/player-game-info", produces = MediaType.APPLICATION_JSON_VALUE)
  public List<PlayerPropsEntity> getPlayerGameInfo(@RequestParam Map<String, String> params) {

    EntityManager em = null;
    List<PlayerPropsEntity> result = null;
    try {
      em = emf.createEntityManager();
      em.getTransaction().begin();
      TypedQuery<PlayerPropsEntity> query = em.createQuery("SELECT e FROM PlayerGameEntity e", PlayerPropsEntity.class);
      if (query.getResultList() != null) {
        result = query.getResultList();
      }
    } catch (Exception e) {
      log.error(e.getMessage());
    }
    return result;
  }  

  // dynamically fetches player props data
  @PostMapping(value = "/fetch-player-props", produces = MediaType.APPLICATION_JSON_VALUE)
  public List<PlayerStatsResponse> getPlayerProps(@RequestBody GenericRequestBody request) throws Exception {
    log.info("START: GET Player Props");
    Instant startTime = Instant.now();
    List<PlayerStatsResponse> data = playerPropsService.getPlayerProps(request);
    Instant endTime = Instant.now();
    log.info("END: GET Player Props, timeElasped: {}", Duration.between(startTime, endTime).toMillis());
    return data;
  }

  // gets player props against opponent
  @PostMapping(value = "/fetch-props-against-opponent-prize-picks", produces = MediaType.APPLICATION_JSON_VALUE)
  public List<LineHistory> getPropsAgainstOpponentPrizePicks(@RequestBody GenericRequestBody request, @RequestParam String statType, @RequestParam Double lineScore) throws Exception {
    log.info("START: GET Player Props");
    Instant startTime = Instant.now();
    List<PlayerStatsResponse> data = playerPropsService.getPlayerProps(request);
    List<LineHistory> mappedData = picksProjectionsService.getPlayerPropsAgainstOpponent(data, statType, lineScore);
    Instant endTime = Instant.now();
    log.info("END: GET Player Props, timeElasped: {}", Duration.between(startTime, endTime).toMillis());
    return mappedData;
  }

  @PostMapping(value = "/fetch-props-against-opponent-underdog", produces = MediaType.APPLICATION_JSON_VALUE)
  public List<LineHistory> getPropsAgainstOpponentUnderdog(@RequestBody GenericRequestBody request, @RequestParam String statType, @RequestParam Double lineScore) throws Exception {
    log.info("START: GET Player Props");
    Instant startTime = Instant.now();
    List<PlayerStatsResponse> data = playerPropsService.getPlayerProps(request);
    List<LineHistory> mappedData = underdogProjectionsServiceImpl.getPlayerPropsAgainstOpponent(data, statType, lineScore);
    Instant endTime = Instant.now();
    log.info("END: GET Player Props, timeElasped: {}", Duration.between(startTime, endTime).toMillis());
    return mappedData;
  }

  // this method saves player prop information in a time range up to the current date
  @PostMapping(value = "/save-player-props-date-range", produces = MediaType.APPLICATION_JSON_VALUE)
  public SuccessfulSaveResponse postPlayerGameInfo(@RequestParam Map<String, String> params) throws Exception {
    log.info("START: POST Save Player Game");
    SuccessfulSaveResponse saveResponse = playerPropsService.savePlayerGames(params);
    log.info("END: POST Save Player Game");
    return saveResponse;
  }
  
  // fetches prize picks projections
  @GetMapping(value = "/fetch-prize-picks-projections", produces = MediaType.APPLICATION_JSON_VALUE)
  public ProjectionsResponse getPrizePicksProjections() throws Exception {
    log.info("START: GET Prize Picks Projections");
    Instant startTime = Instant.now();
    Long lastUpload = Duration.between(DateUtil.prizePickLastUpdated, startTime).toMillis();
    log.info("Time difference between last upload and now {}ms", lastUpload);
    if (lastUpload < 6000) Thread.sleep(6000 - lastUpload);
    ProjectionsResponse data = picksProjectionsService.getPlayerProjections();
    data.setLastUpdated(DateUtil.formatInstant(DateUtil.prizePickLastUpdated));
    // log.info("END: GET Prize Picks Projections, timeElasped: {}", Duration.between(startTime, endTime).toMillis());
    return data;
  }

  @GetMapping(value = "/fetch-underdog-projections", produces = MediaType.APPLICATION_JSON_VALUE)
  public ProjectionsResponse getUnderdogProjections() throws Exception {
    log.info("START: GET Underdog Projections");
    Instant startTime = Instant.now();
    Long lastUpload = Duration.between(DateUtil.underdogLastUpdated, startTime).toMillis();
    log.info("Time difference between last upload and now {}ms", lastUpload);
    if (lastUpload < 6000) Thread.sleep(6000 - lastUpload);
    ProjectionsResponse data = underdogProjectionsServiceImpl.getPlayerProjections();
    data.setLastUpdated(DateUtil.formatInstant(DateUtil.underdogLastUpdated));
    // log.info("END: GET Underdog Projections, timeElasped: {}", Duration.between(startTime, endTime).toMillis());
    return data;
  }
  
  @GetMapping(value = "/player-props-normalize", produces = "application/json")
  public void normalizePlayerGames() {
    log.info("START: Normalizing Player Games");
    playerPropsProc.normalize();
    log.info("END: Normalizing PlayerGames");
  }
  // @PostMapping(value = "/fetch-player-game-data", produces = MediaType.APPLICATION_JSON_VALUE)
  // public List<PlayerGameFactEntity> getData(@RequestBody GenericRequestBody request) throws Exception {
  //   log.info("START: GET Player Game");
  //   Instant startTime = Instant.now();
  //   List<PlayerGameFactEntity> data = playerGameService.getPlayerGames(request);
  //   Instant endTime = Instant.now();
  //   log.info("END: GET Player Game, timeElasped: {}", Duration.between(startTime, endTime).toMillis());
  //   return data;
  // }

  // this method triggers the daily job to get the most updated  player-prop data
  // @PostMapping(value = "/player-game-cron-job", produces = MediaType.APPLICATION_JSON_VALUE)
  // public SuccessfulSaveResponse startJob() throws Exception {
  //   log.info("START: Starting Cron Job For Player Game Info");
  //   SuccessfulSaveResponse saveResponse = playerGameService.startJob();
  //   log.info("START: Starting Cron Job For Player Game Info");
  //   return saveResponse;
  // }

}
