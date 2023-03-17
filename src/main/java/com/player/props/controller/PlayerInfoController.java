package com.player.props.controller;

import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.player.props.dao.PlayerInfoEntity;
import com.player.props.model.request.BDLPlayerInfo;
import com.player.props.model.request.BDLPlayerInfoResponse;
import com.player.props.model.response.SuccessfulSaveResponse;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/api/v1")
public class PlayerInfoController {

  @Autowired
  EntityManagerFactory emf;

  @GetMapping(value = "/player-info", produces = "application/json")
  public List<PlayerInfoEntity> getPlayerInfo(@RequestParam Map<String, String> params) {
    EntityManager em = emf.createEntityManager();
    em.getTransaction().begin();
    TypedQuery<PlayerInfoEntity> query = em.createQuery("SELECT e FROM PlayerInfoEntity e", PlayerInfoEntity.class);

    List<PlayerInfoEntity> result = query.getResultList();
    return result;
  }

  @PostMapping(value = "/player-info", produces="application/json")
  public SuccessfulSaveResponse postPlayerInfo(@RequestParam Map<String, String> params) {

    int page = 121;
    boolean cont = true;
    SuccessfulSaveResponse saveResponse = new SuccessfulSaveResponse();
    do {
      String url = "https://www.balldontlie.io/api/v1/players";
      StringBuilder str = new StringBuilder();
      str.append(url).append("?");
      str.append("page").append("=").append(page);

      String newUrl = str.toString();
      RestTemplate restTemplate = new RestTemplate();
      ResponseEntity<BDLPlayerInfoResponse> response = restTemplate.getForEntity(newUrl, BDLPlayerInfoResponse.class);

      List<BDLPlayerInfo> playerInfos = response.getBody().getData();

      if (response.getBody().getMeta().getNext_page() == null) {
        cont = false;
      }
      EntityManager em = emf.createEntityManager();
      try {
        // em = emf.createEntityManager();
        em.getTransaction().begin();
        playerInfos.stream().forEach(playerInfo -> {
          PlayerInfoEntity entity = new PlayerInfoEntity();
          entity.setTeam_id(Integer.toString(playerInfo.getTeam().getId()));
          entity.setFirst_name(playerInfo.getFirst_name());
          entity.setLast_name(playerInfo.getLast_name());
          entity.setPosition(playerInfo.getPosition());
          entity.setPlayer_id(Integer.toString(playerInfo.getId()));
          em.persist(entity);
      });
      em.getTransaction().commit();
      if (saveResponse.getRecordsSaved() == null) {
        saveResponse.setRecordsSaved(playerInfos.size());
      }
      saveResponse.setRecordsSaved(saveResponse.getRecordsSaved() + playerInfos.size());
      log.info("Saved {} player info records | On page {}.", saveResponse.getRecordsSaved(), page);
      page += 1;
      } catch (Exception e) {
        log.error(e.getMessage());
      } finally {
        if (em != null) {
          em.close();
        }
      }
    } while (cont);

    return saveResponse;
  }
}
