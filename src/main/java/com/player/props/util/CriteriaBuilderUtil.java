package com.player.props.util;

import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.player.props.dao.PlayerGameEntity;
import com.player.props.dao.PlayerGameFactEntity;

public class CriteriaBuilderUtil {

  public static Map<String, Predicate> buildWherePredicate(Root<?> root, CriteriaBuilder criteriaBuilder, Map<String, Map<String, Object>> whereMap) throws Exception {
    // you can use a Predicate list and ues criteriaBuilder.and to chain them
    // together
    Map<String, Predicate> predicateMap = new HashMap<>();
    for (Map.Entry<String, Map<String, Object>> condition : whereMap.entrySet()) {
      String andOrCondition = condition.getKey();
      List<Predicate> conditionPredicate = new ArrayList<Predicate>();
      for (Map.Entry<String, Object> entry : condition.getValue().entrySet()) {
        String key = entry.getKey();
        Object value = entry.getValue();
        // use root.get(key), key in this cause is the column name, and root is the
        // table (entity);
        // first thing I have to do
        // 1. Create the predicate through criteriaBuilder
        // 2. Add each predicate into a predicate list, then merge them
        Predicate predicate;
        if (value instanceof List) {
          predicate = root.get(key).in(value);
        } else {
          predicate = criteriaBuilder.equal(root.get(key), value);
        }
        conditionPredicate.add(predicate);
      }
      Predicate joinPredicate;
      if (andOrCondition.equals("and")) {
        joinPredicate = criteriaBuilder.and(conditionPredicate.toArray(new Predicate[] {}));
        predicateMap.put("and", joinPredicate);
      } else if (andOrCondition.equals("or")) {
        joinPredicate = criteriaBuilder.or(conditionPredicate.toArray(new Predicate[] {}));
        predicateMap.put("or", joinPredicate);
      } else {
        throw new Exception("Error with where clause. needs to be 'and' or 'or");
      }
    }
    return predicateMap;
    // return predicateArr;
  }

  public static Predicate buildDatesPredicate(Root<?> root, CriteriaBuilder criteriaBuilder, String startDate, String endDate) {
    List<Predicate> conditionsList = new ArrayList<Predicate>();
    Predicate startDatePred = criteriaBuilder.greaterThanOrEqualTo(root.<Date>get("date"), Date.valueOf(startDate));
    // Predicate startDatePred = criteriaBuilder.greaterThanOrEqualTo(root.get("date"), startDate);
    conditionsList.add(startDatePred);
    Predicate endDatePred = criteriaBuilder.lessThanOrEqualTo(root.<Date>get("date"), Date.valueOf(endDate));
    // Predicate endDatePred = criteriaBuilder.lessThan(root.get("date"), endDate);
    conditionsList.add(endDatePred);
    return criteriaBuilder.and(conditionsList.toArray(new Predicate[]{}));
    // return conditionsList;
  }

  public static List<Order> buildOrderByPredicate(Root<?> root, CriteriaBuilder criteriaBuilder,
      Map<String, String> orderByMap) {
    List<Order> orderByList = new ArrayList<Order>();
    for (Map.Entry<String, String> entry : orderByMap.entrySet()) {
      String key = entry.getKey();
      String value = entry.getValue();

      Order order;
      if (value.equals("asc")) {
        order = criteriaBuilder.asc(root.get(key));
      } else {
        order = criteriaBuilder.desc(root.get(key));
      }
      orderByList.add(order);
    }
    return orderByList;
  }
}
