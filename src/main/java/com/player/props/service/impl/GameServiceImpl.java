package com.player.props.service.impl;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.player.props.dao.GamesEntity;
import com.player.props.model.request.BDLGameInfo;
import com.player.props.model.request.BDLGameInfoResponse;
import com.player.props.model.request.MetaInfo;
import com.player.props.model.response.SuccessfulSaveResponse;
import com.player.props.service.GameService;
import com.player.props.util.BdlUtil;
import static com.player.props.util.DateUtil.*;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class GameServiceImpl implements GameService {

  @Autowired
  EntityManagerFactory emf;

  private static String BDL_ATTRIBUTE = "games";

  public SuccessfulSaveResponse startJob() throws Exception {
    SuccessfulSaveResponse saveResponse = new SuccessfulSaveResponse();

    log.info("Starting Game Info Job");
    int page = 1;
    String url = BdlUtil.buildUrl(yesterdayStr(), page, BDL_ATTRIBUTE);
    log.info("Calling -> {}", url);

    BDLGameInfoResponse response = getResponse(url);
    List<BDLGameInfo> data = response.getData();
    MetaInfo meta = response.getMeta();

    saveEntities(data, saveResponse);

    if (meta.getNext_page() != null) {
      getMorePages(url, page + 1, saveResponse);
    }
    saveResponse.setSavedSuccessfully(true);
    log.info("Ending Game Info Job, saved {} records", saveResponse.getRecordsSaved());
    return saveResponse;
  }

  public SuccessfulSaveResponse saveGames() throws InterruptedException, ParseException, Exception {
    int page = 1;
    boolean cont = true;
    SuccessfulSaveResponse saveResponse = new SuccessfulSaveResponse();
    String date = "2023-03-17";

    do {
      String url = BdlUtil.buildUrl(date, page, BDL_ATTRIBUTE);
      log.info("Calling endpiont -> {}", url);

      BDLGameInfoResponse response = getResponse(url);
      List<BDLGameInfo> data = response.getData();
      MetaInfo meta = response.getMeta();

      if (checkIsCurrentDate(date)) {
        saveResponse.setSavedSuccessfully(true);
        break;
      }

      saveEntities(data, saveResponse);

      if (meta.getNext_page() != null) {
        getMorePages(url, page + 1, saveResponse);
      }

      date = iterateDate(date);
      Thread.sleep(1001);
    } while (cont);

    return saveResponse;
  }

  private void getMorePages(String url, int page, SuccessfulSaveResponse saveResponse) throws InterruptedException {
    boolean cont = true;
    do {
      StringBuilder str = new StringBuilder(url);
      str.append("&page=").append(page);
      String newUrl = str.toString();
      log.info("Calling -> {}", newUrl);

      BDLGameInfoResponse response = getResponse(newUrl);
      List<BDLGameInfo> data = response.getData();
      MetaInfo meta = response.getMeta();

      saveEntities(data, saveResponse);
      if (meta.getNext_page() == null) {
        break;
      }
      page += 1;
      Thread.sleep(1001);
    } while (cont);
  }

  private void saveEntities(List<BDLGameInfo> data, SuccessfulSaveResponse saveResponse) {
    EntityManager em = emf.createEntityManager();
    try {
      em.getTransaction().begin();
      for (BDLGameInfo info : data) {
        GamesEntity entity = mapEntity(info);
        em.persist(entity);
      }
      em.getTransaction().commit();
      BdlUtil.logTransaction(saveResponse, data, "Game Info");
      Thread.sleep(1000);
    } catch (Exception e) {
      log.error(e.getMessage());
    } finally {
      if (em != null) {
        em.close();
      }
    }
  }

  private GamesEntity mapEntity(BDLGameInfo info) throws ParseException {
    // DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    // LocalDate localDate = LocalDate.parse(info.getDate(), formatter);
    GamesEntity entity = new GamesEntity();
    entity.setGame_id(info.getId());
    java.util.Date parsed = dateFormat.parse(info.getDate());
    entity.setDate(new Date(parsed.getTime()));
    entity.setHt_id(Integer.toString(info.getHome_team().getId()));
    entity.setHt_score(info.getHome_team_score());
    entity.setAt_id(Integer.toString(info.getVisitor_team().getId()));
    entity.setAt_score(info.getVisitor_team_score());
    entity.setPostseason(info.isPostseason());
    entity.setSeason(info.getSeason());
    return entity;
  }

  private BDLGameInfoResponse getResponse(String url) {
    RestTemplate restTemplate = new RestTemplate();
    ResponseEntity<BDLGameInfoResponse> responseEntity = restTemplate.getForEntity(url, BDLGameInfoResponse.class);
    return responseEntity.getBody();
  }
}
