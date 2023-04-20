package com.player.props.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.player.props.dao.PlayerInfoDistinctEntity;
import com.player.props.dao.PlayerInfoEntity;
import com.player.props.model.request.BDLPlayerInfo;
import com.player.props.model.request.BDLPlayerInfoResponse;
import com.player.props.model.request.GenericRequestBody;
import com.player.props.model.request.MetaInfo;
import com.player.props.model.response.SuccessfulSaveResponse;
import com.player.props.service.PlayerInfoService;
import com.player.props.util.BdlUtil;
import com.player.props.util.CriteriaBuilderUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
// @Component
public class PlayerInfoServiceImpl implements PlayerInfoService {

  private static String BDL_ATTRIBUTE = "players";

  @Autowired
  EntityManagerFactory emf;

  @Cacheable(value = "playerInfo")
  @Override
  public List<PlayerInfoDistinctEntity> getPlayerData(GenericRequestBody request) {
  Map<String, Map<String, Object>> whereMap = request.getWhere();
    Map<String, String> orderByMap = request.getOrderBy();
    String startDate = request.getStart_date();
    String endDate = request.getEnd_date();
    Integer limit = Integer.valueOf(request.getLimit());

    List<PlayerInfoDistinctEntity> result = new ArrayList<>();
    EntityManager em = null;
    try {
      em = emf.createEntityManager();
      CriteriaBuilder cb = em.getCriteriaBuilder();
      CriteriaQuery<PlayerInfoDistinctEntity> query = cb.createQuery(PlayerInfoDistinctEntity.class);
      Root<PlayerInfoDistinctEntity> root = query.from(PlayerInfoDistinctEntity.class);

      List<Predicate> wherePredicates = new ArrayList<>();
      if (whereMap.size() > 0) {
        Map<String, Predicate> predicateCondMap = CriteriaBuilderUtil.buildWherePredicate(root, cb, whereMap);
        if (predicateCondMap.containsKey("and")) {
          wherePredicates.add(predicateCondMap.get("and"));
        }
        ;
        if (predicateCondMap.containsKey("or")) {
          wherePredicates.add(predicateCondMap.get("or"));
        }
        ;
      }
      if (startDate != null && endDate != null) {
        wherePredicates.add(CriteriaBuilderUtil.buildDatesPredicate(root, cb, startDate, endDate));
      }

      query.select(root);
      query.where(wherePredicates.toArray(new Predicate[] {}));

      if (orderByMap.size() > 0) {
        List<Order> orderByList = CriteriaBuilderUtil.buildOrderByPredicate(root, cb, orderByMap);
        query.orderBy(orderByList);
      }
      TypedQuery<PlayerInfoDistinctEntity> typedQuery = em.createQuery(query);
      if (limit > 0) {
        typedQuery.setMaxResults(limit);
      }
      result = typedQuery.getResultList().stream().filter(entity -> !entity.getPosition().isBlank()).collect(Collectors.toList());

    } catch (Exception e) {
      log.error("Error fetching Player Information | Error msg: {}", e.getMessage());
    } finally {
      if (em != null) {
        em.close();
      }
    }
    return result;
  }

  @Override
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

  @CacheEvict(value = "playerInfo", allEntries = true)
  @Scheduled(fixedRate = 86400000)
  public void emptyPlayerInfoCache() {
    log.info("emptying playerInfo cache");
  }

  @CacheEvict(value = "teamInfo", allEntries = true)
  @Scheduled(fixedRate = 86400000)
  public void emptyTeamInfoCache() {
    log.info("emptying teamInfo cache");
  }
}
