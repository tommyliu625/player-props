package com.player.props.service.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.player.props.dao.PlayerInfoEntity;
import com.player.props.model.request.BDLPlayerInfo;
import com.player.props.model.request.BDLPlayerInfoResponse;
import com.player.props.model.request.MetaInfo;
import com.player.props.model.response.SuccessfulSaveResponse;
import com.player.props.util.BdlUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
// @Component
public class PlayerInfoServiceImpl {

  private static String BDL_ATTRIBUTE = "players";

  @Autowired
  EntityManagerFactory emf;

  public SuccessfulSaveResponse savePlayerInfo() {
    int page = 1;
    SuccessfulSaveResponse saveResponse = new SuccessfulSaveResponse();
    boolean cont = true;
    do {

      String newUrl = BdlUtil.buildUrl("", page, BDL_ATTRIBUTE);
      log.info("Calling -> {}", newUrl);

      BDLPlayerInfoResponse response = getResponse(newUrl);

      List<BDLPlayerInfo> data = response.getData();
      MetaInfo meta = response.getMeta();

      saveEntities(data, saveResponse);

      if (meta.getNext_page() != null) {
        page += 1;
      } else {
        saveResponse.setSavedSuccessfully(true);
        break;
      }

    } while (cont);
    return saveResponse;
  }


  private void saveEntities(List<BDLPlayerInfo> data, SuccessfulSaveResponse saveResponse) {
    EntityManager em = emf.createEntityManager();
    try {
      em.getTransaction().begin();
      for (BDLPlayerInfo info : data) {
        PlayerInfoEntity entity = mapEntity(info);
        em.persist(entity);
      }
      em.getTransaction().commit();
      BdlUtil.logTransaction(saveResponse, data, "Player Info");
      Thread.sleep(1001);
    } catch (Exception e) {
      log.error(e.getMessage());
    } finally {
      if (em != null) {
        em.close();
      }
    }

  }

  private PlayerInfoEntity mapEntity(BDLPlayerInfo playerInfo) {
    PlayerInfoEntity entity = new PlayerInfoEntity();
    entity.setTeam_id(Integer.toString(playerInfo.getTeam().getId()));
    entity.setFirst_name(playerInfo.getFirst_name());
    entity.setLast_name(playerInfo.getLast_name());
    entity.setPosition(playerInfo.getPosition());
    entity.setPlayer_id(Integer.toString(playerInfo.getId()));
    return entity;
  }

  private BDLPlayerInfoResponse getResponse(String newUrl) {
    RestTemplate restTemplate = new RestTemplate();
    ResponseEntity<BDLPlayerInfoResponse> response = restTemplate.getForEntity(newUrl, BDLPlayerInfoResponse.class);
    return response.getBody();
  }
}
