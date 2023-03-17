package com.player.props.service.impl;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.player.props.dao.PlayerGameEntity;
import com.player.props.model.request.BDLPlayerGameInfo;
import com.player.props.model.request.BDLPlayerGameInfoResponse;
import com.player.props.model.request.MetaInfo;
import com.player.props.model.response.SuccessfulSaveResponse;
import com.player.props.service.PlayerGameService;
import com.player.props.util.BdlUtil;
import com.player.props.util.DateUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PlayerGameServiceImpl implements PlayerGameService {

  @Autowired
  EntityManagerFactory emf;

  private static String BDL_ATTRIBUTE = "stats";

  public SuccessfulSaveResponse savePlayerGames() throws Exception {
    int page = 1;
    SuccessfulSaveResponse saveResponse = new SuccessfulSaveResponse();
    String date = "2022-10-18";
    boolean cont = true;
    do {

      String newUrl = BdlUtil.buildUrl(date, page, BDL_ATTRIBUTE);
      log.info("Calling -> {}", newUrl);

      BDLPlayerGameInfoResponse response = getResponse(newUrl);
      List<BDLPlayerGameInfo> data = response.getData();
      MetaInfo meta = response.getMeta();

      saveEntities(data, saveResponse);

      if (meta.getNext_page() != null) {
        getMorePages(newUrl, page + 1, saveResponse);
      }
      date = DateUtil.iterateDate(date);

    } while (cont);
    return saveResponse;
  }

  private BDLPlayerGameInfoResponse getResponse(String newUrl) {
    BDLPlayerGameInfoResponse response = null;
    try {
      RestTemplate restTemplate = new RestTemplate();
      ResponseEntity<BDLPlayerGameInfoResponse> responseEntity = restTemplate.getForEntity(newUrl,
        BDLPlayerGameInfoResponse.class);
        response = responseEntity.getBody();
    } catch (Exception e) {
      log.error("Error fetching {} with error message {}", newUrl, e.getMessage());
    }
    return response;
  }

  private void getMorePages(String url, int page, SuccessfulSaveResponse saveResponse) throws Exception {
    boolean cont = true;
    do {
      StringBuilder str = new StringBuilder(url);
      str.append("&page=").append(page);
      String newUrl = str.toString();
      log.info("Calling -> {}", newUrl);

      BDLPlayerGameInfoResponse response = getResponse(newUrl);
      List<BDLPlayerGameInfo> data = response.getData();
      MetaInfo meta = response.getMeta();

      saveEntities(data, saveResponse);
      if (meta.getNext_page() == null) {
        break;
      }
      page += 1;
    } while(cont);
  }

  private void saveEntities(List<BDLPlayerGameInfo> data, SuccessfulSaveResponse saveResponse) {
    EntityManager em = emf.createEntityManager();
    try {
      em.getTransaction().begin();
      for (BDLPlayerGameInfo info : data) {
        PlayerGameEntity entity = mapEntity(info);
        em.persist(entity);
      }
      em.getTransaction().commit();
      BdlUtil.logTransaction(saveResponse, data, "Player Game Info");
      Thread.sleep(2000);
    } catch (Exception e) {
      log.error(e.getMessage());
    } finally {
      if (em != null) {
        em.close();
      }
    }
  }

  private PlayerGameEntity mapEntity(BDLPlayerGameInfo info) throws Exception {
    PlayerGameEntity entity = new PlayerGameEntity();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    entity.setPlayer_game_id(Integer.toString(info.getId()));
    entity.setPlayer_id(Integer.toString(info.getPlayer().getId()));
    entity.setGame_id(info.getGame().getId());
    entity.setTeam_id(Integer.toString(info.getTeam().getId()));
    java.util.Date parsed = dateFormat.parse(info.getGame().getDate());
    entity.setDate(new Date(parsed.getTime()));
    entity.setPts(info.getPts());
    entity.setRbs(info.getReb());
    entity.setAsts(info.getAst());
    entity.setFg3m(info.getFg3m());
    entity.setBlks(info.getBlk());
    entity.setStls(info.getStl());
    entity.setTos(info.getTurnover());
    entity.setPostseason(info.getGame().isPostseason());
    entity.setSeason(info.getGame().getSeason());
    return entity;
  }
}