package com.player.props.service.impl;

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
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.player.props.dao.PlayerPropsEntity;
import com.player.props.dao.PlayerPropsFactEntity;
import com.player.props.model.request.BDLPlayerGameInfo;
import com.player.props.model.request.BDLPlayerGameInfoResponse;
import com.player.props.model.request.GenericRequestBody;
import com.player.props.model.request.MetaInfo;
import com.player.props.model.response.PlayerStatsResponse;
import com.player.props.model.response.SuccessfulSaveResponse;
import com.player.props.util.BdlUtil;
import com.player.props.util.CriteriaBuilderUtil;
import com.player.props.util.mappers.PlayerStatsEntityToResponseMapper;

import static com.player.props.util.DateUtil.*;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PlayerPropsServiceImpl {
    @Autowired
  EntityManagerFactory emf;

  private static String BDL_ATTRIBUTE = "stats";
    
  // this fetches the player props data using a dynamic query
  public List<PlayerStatsResponse> getPlayerProps(GenericRequestBody requestBody) throws Exception {
    List<PlayerPropsFactEntity> result = new ArrayList<>();
    Map<String, Map<String, Object>> whereMap = requestBody.getWhere();
    Map<String, String> orderByMap = requestBody.getOrderBy();
    Integer limit = Integer.valueOf(requestBody.getLimit());
    Integer offset = Integer.valueOf(requestBody.getOffset());
    String startDate = requestBody.getStart_date();
    String endDate = requestBody.getEnd_date();

    EntityManager em = null;
    try {
      em = emf.createEntityManager();
      CriteriaBuilder cb = em.getCriteriaBuilder();
      CriteriaQuery<PlayerPropsFactEntity> query = cb.createQuery(PlayerPropsFactEntity.class);
      Root<PlayerPropsFactEntity> root = query.from(PlayerPropsFactEntity.class);


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
      TypedQuery<PlayerPropsFactEntity> typedQuery = em.createQuery(query);
      if (Integer.valueOf(limit) > 0) {
        typedQuery.setFirstResult(offset);
        typedQuery.setMaxResults(limit);
      }
      result = typedQuery.getResultList();
    } catch (Exception e) {
      log.error("Error fetching Player Props Information | Error msg: {}", e.getMessage());
    } finally {
      if (em != null) {
        em.close();
      }
    }

    return result.stream().map(PlayerStatsEntityToResponseMapper::map)
        .toList();
  }

  // this method gets the player game info for the last day
  // used during the cron job
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

  // this method gets the player game info in a time range up to the current date
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
      date = iterateDate(date);

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
        PlayerPropsEntity entity = mapEntity(info);
        if (entity == null) {
          continue;
        }
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

  private PlayerPropsEntity mapEntity(BDLPlayerGameInfo info) throws Exception {
    try {
      PlayerPropsEntity entity = new PlayerPropsEntity();
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
      entity.setFg3a(info.getFg3a());
      entity.setFg3m(info.getFg3m());
      entity.setFg3_pct(info.getFg3_pct());
      entity.setFg_pct(info.getFg_pct());
      entity.setFga(info.getFga());
      entity.setFgm(info.getFgm());
      entity.setFt_pct(info.getFt_pct());
      entity.setFta(info.getFta());
      entity.setFtm(info.getFtm());
      entity.setBlks(info.getBlk());
      entity.setStls(info.getStl());
      entity.setTos(info.getTurnover());
      entity.setPostseason(info.getGame().isPostseason());
      entity.setSeason(info.getGame().getSeason());
      entity.setOreb(info.getOreb());
      entity.setDreb(info.getDreb());
      entity.setPf(info.getPf());
      String minStr = info.getMin().contains(":") ? info.getMin().split(":")[0] : info.getMin();    
      entity.setMin(Integer.parseInt(minStr == "" ? "0" : minStr));
      return entity;
    } catch (Exception e) {
      return null;
    }
    
  }

  @CacheEvict(value = "playerGameInfo", allEntries = true)
  @Scheduled(fixedRate = 86400000)
  public void emptyPlayerGameInfoCache() {
    log.info("emptying playerGameInfo cache");
  }
}
