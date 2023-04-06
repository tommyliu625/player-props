package com.player.props.controller;

import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
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

import com.player.props.dao.TeamInfoEntity;
import com.player.props.model.request.BDLTeamInfoResponse;
import com.player.props.model.request.GenericRequestBody;
import com.player.props.model.request.BDLTeamInfo;
import com.player.props.model.response.SuccessfulSaveResponse;

import lombok.extern.slf4j.Slf4j;

@CrossOrigin
@RestController
@RequestMapping("/api/v1")
@Slf4j
public class TeamInfoController {

  @Autowired
  EntityManagerFactory entityManagerFactory;


  @PostMapping(value = "/team-info-data", produces = "application/json")
  public List<?> getTeamInfo(@RequestBody GenericRequestBody request) {
    List<?> list = null;
    EntityManager entityManager = null;
    log.info("START: GET Team Info");
    try {
      entityManager = entityManagerFactory.createEntityManager();
      entityManager.getTransaction().begin();
      Query query = entityManager.createQuery("SELECT e FROM TeamInfoEntity e");
      list = query.getResultList();
    } catch (Exception exception) {
      log.error(exception.getMessage());
    } finally {
      if (entityManager != null) {
        entityManager.close();
      }
    }
    log.info("END: GET Team Info");
    return list;

  }

  @PostMapping(value = "/team-info", produces="application/json")
  public SuccessfulSaveResponse postTeamInfo(@RequestParam Map<String, String> params) {
    String url = "https://www.balldontlie.io/api/v1/teams";
    StringBuilder str = new StringBuilder();
    str.append(url).append("?");
    params.forEach((key, value) -> {
      str.append(key).append("=").append(value).append("&");
    });
    String newUrl = str.toString();
    RestTemplate restTemplate = new RestTemplate();
    ResponseEntity<BDLTeamInfoResponse> response = restTemplate.getForEntity(newUrl, BDLTeamInfoResponse.class);

    List<BDLTeamInfo> teamInfoList = response.getBody().getData();

    EntityManager entityManager = entityManagerFactory.createEntityManager();
    entityManager.getTransaction().begin();
    for (BDLTeamInfo teamInfo : teamInfoList) {
      TeamInfoEntity entity = new TeamInfoEntity();
      entity.setAbbreviation(teamInfo.getAbbreviation());
      entity.setCity(teamInfo.getCity());
      entity.setConference(teamInfo.getConference());
      entity.setDivision(teamInfo.getDivision());
      entity.setFull_name(teamInfo.getFull_name());
      entity.setTeam_id(Integer.toString(teamInfo.getId()));
      entity.setName(teamInfo.getName());
      entityManager.persist(entity);
    }
    SuccessfulSaveResponse saveResponse = new SuccessfulSaveResponse();
    try {
      entityManager.getTransaction().commit();
      saveResponse.setRecordsSaved(teamInfoList.size());
      saveResponse.setSavedSuccessfully(true);
    } catch (Exception e) {
      log.error(e.getMessage());
      saveResponse.setSavedSuccessfully(false);
    } finally {
      entityManager.close();
    }
    return saveResponse;
  }
}
