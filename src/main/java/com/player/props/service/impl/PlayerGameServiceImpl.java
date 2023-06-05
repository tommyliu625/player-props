package com.player.props.service.impl;

import static com.player.props.util.DateUtil.checkIsCurrentDate;
import static com.player.props.util.DateUtil.yesterdayStr;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

import com.player.props.dao.PlayerGameEntity;
import com.player.props.dao.PlayerGameFactEntity;
import com.player.props.model.request.BDLPlayerGameInfo;
import com.player.props.model.request.BDLPlayerGameInfoResponse;
import com.player.props.model.request.GenericRequestBody;
import com.player.props.model.request.MetaInfo;
import com.player.props.model.response.SuccessfulSaveResponse;
import com.player.props.service.PlayerGameService;
import com.player.props.util.BdlUtil;
import com.player.props.util.CriteriaBuilderUtil;
import com.player.props.util.DateUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PlayerGameServiceImpl implements PlayerGameService {

  @Autowired
  EntityManagerFactory emf;

  private static String BDL_ATTRIBUTE = "stats";

  @Cacheable(value = "playerGameInfo")
  @Override
  public List<PlayerGameFactEntity> getPlayerGames(GenericRequestBody requestBody) throws Exception {
    List<PlayerGameFactEntity> result = null;
    Map<String, Map<String, Object>> whereMap = requestBody.getWhere();
    Map<String, String> orderByMap = requestBody.getOrderBy();
    Integer limit = Integer.valueOf(requestBody.getLimit());
    String startDate = requestBody.getStart_date();
    String endDate = requestBody.getEnd_date();

    EntityManager em = null;
    try {
      em = emf.createEntityManager();
      CriteriaBuilder cb = em.getCriteriaBuilder();
      CriteriaQuery<PlayerGameFactEntity> query = cb.createQuery(PlayerGameFactEntity.class);
      Root<PlayerGameFactEntity> root = query.from(PlayerGameFactEntity.class);


      List<Predicate> wherePredicates = new ArrayList<>();
      if (whereMap != null) {
        Map<String, Predicate> predicateCondMap = CriteriaBuilderUtil.buildWherePredicate(root, cb, whereMap);
        if (predicateCondMap.containsKey("and")) {
          wherePredicates.add(predicateCondMap.get("and"));
        };
        if (predicateCondMap.containsKey("or")) {
          wherePredicates.add(predicateCondMap.get("or"));
        };
      }
      if (startDate != null && endDate != null) {
        wherePredicates.add(CriteriaBuilderUtil.buildDatesPredicate(root, cb, startDate, endDate));
      }

      query.select(root);
      query.where(wherePredicates.toArray(new Predicate[] {}));
      if (orderByMap != null) {
        List<Order> orderByList = CriteriaBuilderUtil.buildOrderByPredicate(root, cb, orderByMap);
        query.orderBy(orderByList);
      }
      TypedQuery<PlayerGameFactEntity> typedQuery = em.createQuery(query);
      if (Integer.valueOf(limit) > 0) {
        typedQuery.setMaxResults(limit);
      }
      result = typedQuery.getResultList();
    } catch (Exception e) {
      log.error("Error fetching Player Game Information | Error msg: {}", e.getMessage());
    } finally {
      if (em != null) {
        em.close();
      }
    }
    return result;
  }


  @Override
  public SuccessfulSaveResponse startJob() throws Exception {
    SuccessfulSaveResponse saveResponse = new SuccessfulSaveResponse();
    log.info("Starting Player Game Job");

    int page = 1;
    String url = BdlUtil.buildUrl(yesterdayStr(), page, BDL_ATTRIBUTE);
    log.info("Calling -> {}", url);

    BDLPlayerGameInfoResponse response = getResponse(url);
    List<BDLPlayerGameInfo> data = response.getData();
    MetaInfo meta = response.getMeta();

    saveEntities(data, saveResponse);

    if (meta.getNext_page() != null) {
      getMorePages(url, page + 1, saveResponse);
    }
    saveResponse.setSavedSuccessfully(true);
    log.info("Ending Player Info Job, saved {} records", saveResponse.getRecordsSaved());
    return saveResponse;
  }

  @Override
  public SuccessfulSaveResponse savePlayerGames(Map<String, String> params) throws Exception {
    int page = 1;
    SuccessfulSaveResponse saveResponse = new SuccessfulSaveResponse();
    String date = params.get("date");
    boolean cont = true;
    do {

      String newUrl = BdlUtil.buildUrl(date, page, BDL_ATTRIBUTE);
      log.info("Calling -> {}", newUrl);

      BDLPlayerGameInfoResponse response = getResponse(newUrl);
      List<BDLPlayerGameInfo> data = response.getData();
      MetaInfo meta = response.getMeta();

      if (checkIsCurrentDate(date)) {
        saveResponse.setSavedSuccessfully(true);
        break;
      }

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
    } while (cont);
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

  @CacheEvict(value = "playerGameInfo", allEntries = true)
  @Scheduled(fixedRate = 86400000)
  public void emptyPlayerGameInfoCache() {
    log.info("emptying playerGameInfo cache");
  }
}
