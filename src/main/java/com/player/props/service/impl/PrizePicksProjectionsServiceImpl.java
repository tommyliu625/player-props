package com.player.props.service.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.player.props.dao.FullName;
import com.player.props.dao.PlayerPropsFactEntity;
import com.player.props.dao.PrizePicksProjectionsNbaEntity;
import com.player.props.model.response.PlayerProjectionInformation;
import com.player.props.model.response.PlayerStatsResponse;
import com.player.props.model.response.ProjectionsResponse;
import com.player.props.util.mappers.PlayerStatsEntityToResponseMapper;
import com.player.props.model.response.LineHistory;
import static com.player.props.util.ProjectionUtil.comboToDbMapper;
import static com.player.props.util.ProjectionUtil.prizePicksToDbMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PrizePicksProjectionsServiceImpl {

  @Autowired
  private EntityManagerFactory emf;

  @Autowired
  private DataSource dataSource;

  private static final DecimalFormat df = new DecimalFormat("0.00");

  private static final Integer STREAK = 3;

  public ProjectionsResponse getPlayerProjections() {
    log.info("Fetching player projections");
    EntityManager em = emf.createEntityManager();

    List<PrizePicksProjectionsNbaEntity> projectionsDb = em
        .createQuery("SELECT e FROM PrizePicksProjectionsNbaEntity e", PrizePicksProjectionsNbaEntity.class)
        .getResultList();

    // Key: Full Name | Value: PlayerPropsEntity
    Map<FullName, List<PrizePicksProjectionsNbaEntity>> playerProjMap = projectionsDb.stream()
        .collect(Collectors.groupingBy(PrizePicksProjectionsNbaEntity::getFullName));

    List<FullName> nonComboPlayers = playerProjMap.keySet().stream()
        .filter(fullName -> !(fullName.getFirstName().contains("+") || fullName.getLastName().contains("+")))
        .collect(Collectors.toList());

    // Get Combo Players, player 1 stored in first_name, player 2 stored in
    // last_name
    List<FullName> player1ComboPlayers = playerProjMap.keySet().stream()
        .filter(fullName -> fullName.getFirstName().contains(" "))
        .map(fullName -> {
          String[] names = fullName.firstName.split(" ", 2);
          return FullName.builder().firstName(names[0]).lastName(names[1]).build();
        }).collect(Collectors.toList());

    List<FullName> player2ComboPlayers = playerProjMap.keySet().stream()
        .filter(fullName -> {
          String[] names = fullName.lastName.split(" ");
          Set<String> invalidNames = Set.of("III", "II", "Jr.", "IV");
          if (names.length == 2 && invalidNames.contains(names[1]))
            return false;
          if (names.length > 1)
            return true;
          else
            return false;
        })
        .map(fullName -> {
          String[] names = fullName.lastName.split(" ", 2);
          return FullName.builder().firstName(names[0]).lastName(names[1]).build();
        })
        .collect(Collectors.toList());

    List<FullName> allPlayers = new ArrayList<>();
    allPlayers.addAll(player2ComboPlayers);
    allPlayers.addAll(player1ComboPlayers);
    allPlayers.addAll(nonComboPlayers);
    Set<FullName> uniquePlayers = allPlayers.stream().collect(Collectors.toSet());

    // List of last 10 games for each player
    List<PlayerPropsFactEntity> playersLast10Games = getLastXGamesForPlayers(uniquePlayers.stream().toList());

    // Key: Player ID | Value: List<PlayerPropsEntity>
    // this should give me the most recent 10 games for each player in a map
    Map<String, List<PlayerPropsFactEntity>> playersLast10GamesMap = playersLast10Games.stream()
        .collect(Collectors.groupingBy(entity -> entity.getFullName().toString()));

    // Key: Stat Type| Value: Information on players projections
    Map<String, List<PlayerProjectionInformation>> playerProjectionInformationMap = projectionsDb.stream()
        .filter(entity -> !(entity.getFullName().toString().contains("+"))
            && (prizePicksToDbMapper.containsKey(entity.getStatType())
                || comboToDbMapper.containsKey(entity.getStatType())))
        .collect(Collectors.groupingBy(PrizePicksProjectionsNbaEntity::getStatType,
            Collectors.mapping(entity -> mapToResponse(entity), Collectors.toList())));

    addLineHistory(playerProjectionInformationMap, playersLast10GamesMap);

    em.close();
    return ProjectionsResponse.builder().stat_projections(playerProjectionInformationMap).build();
    // .player_history(playersLast10GamesMap).
  }

  private void addLineHistory(Map<String, List<PlayerProjectionInformation>> playerProjectionInformationMap,
      Map<String, List<PlayerPropsFactEntity>> playersLast10GamesMap) {
    List<PlayerProjectionInformation> hotStreaks = new ArrayList<>();
    List<PlayerProjectionInformation> frequentlyHit = new ArrayList<>();
    for (Map.Entry<String, List<PlayerProjectionInformation>> infoMap : playerProjectionInformationMap.entrySet()) {
      String statType = infoMap.getKey();
      List<PlayerProjectionInformation> infoList = infoMap.getValue();
      for (PlayerProjectionInformation info : infoList) {
        List<PlayerPropsFactEntity> entityList = info.firstName.contains(" ")
            ? playersLast10GamesMap.get(info.getFirstName())
            : playersLast10GamesMap.get(info.getFullName());
        entityList = entityList == null ? new ArrayList<>() : entityList;
        List<LineHistory> lineHistories = new ArrayList<>();
        boolean isCombo = false;
        for (int i = 0; i < entityList.size(); i++) {
          double value = 0;
          if (prizePicksToDbMapper.containsKey(statType)) {
            value = prizePicksToDbMapper.get(statType).apply(entityList.get(i));
          }
          if (comboToDbMapper.containsKey(statType)) {
            isCombo = true;
            List<PlayerPropsFactEntity> comboPlayers = new ArrayList<>();
            List<PlayerPropsFactEntity> firstComboPlayerStats = playersLast10GamesMap.get(info.firstName);
            List<PlayerPropsFactEntity> secondComboPlayerStats = playersLast10GamesMap.get(info.lastName);
            if (firstComboPlayerStats == null || secondComboPlayerStats == null) {
              log.info("Could not find combo stats for {} and {} for index {}", info.firstName, info.lastName, i);
              continue;
            }
            comboPlayers.add(playersLast10GamesMap.get(info.firstName).get(i));
            comboPlayers.add(playersLast10GamesMap.get(info.lastName).get(i));
            value = comboToDbMapper.get(statType).apply(comboPlayers);
          }
          lineHistories.add(LineHistory.builder()
              .date(entityList.get(i).getDate())
              .value(value)
              .hit(Double.valueOf(value).compareTo(info.getLineScore()) >= 0)
              .build());
        }
        info.setLineHistory(lineHistories);
        Integer overStreak = 0;
        Integer overLast3 = 0;
        Integer overLast5 = 0;
        Integer overLast10 = 0;
        Integer underStreak = 0;
        Integer underLast3 = 0;
        Integer underLast5 = 0;
        Integer underLast10 = 0;
        Double avgLast10 = lineHistories.stream().limit(10).mapToDouble(LineHistory::getValue).average().orElse(0);
        Double avgLast5 = lineHistories.stream().limit(5).mapToDouble(LineHistory::getValue).average().orElse(0);
        Double avgLast3 = lineHistories.stream().limit(3).mapToDouble(LineHistory::getValue).average().orElse(0);
        boolean hitStreakStillGoing = true;
        boolean missStreakStillGoing = true;
        for (int i = 0; i < lineHistories.size(); i++) {
          if (i < 3) {
            overLast3 += lineHistories.get(i).getHit() ? 1 : 0;
            underLast3 += !lineHistories.get(i).getHit() ? 1 : 0;
          }
          if (i < 5) {
            overLast5 += lineHistories.get(i).getHit() ? 1 : 0;
            underLast5 += !lineHistories.get(i).getHit() ? 1 : 0;
          }
          if (i < 10) {
            overLast10 += lineHistories.get(i).getHit() ? 1 : 0;
            underLast10 += !lineHistories.get(i).getHit() ? 1 : 0;
          }
          if (hitStreakStillGoing && lineHistories.get(i).getHit()) {
            overStreak += 1;
          } else {
            hitStreakStillGoing = false;
          }
          if (missStreakStillGoing && !lineHistories.get(i).getHit()) {
            underStreak += 1;
          } else {
            missStreakStillGoing = false;
          }
        }

        info.setStatType(statType);
        info.setOverStreak(overStreak);
        info.setOverLast3(overLast3);
        info.setOverLast5(overLast5);
        info.setOverLast10(overLast10);
        info.setUnderStreak(underStreak);
        info.setUnderLast3(underLast3);
        info.setUnderLast5(underLast5);
        info.setUnderLast10(underLast10);
        info.setAvgLast10(df.format(avgLast10));
        info.setAvgLast5(df.format(avgLast5));
        info.setAvgLast3(df.format(avgLast3));
        info.setPlayerId(isCombo || entityList.size() == 0 ? null : entityList.get(0).getPlayer_id());

        boolean trendingUp = avgLast3 > avgLast5 && avgLast5 > avgLast10;
        boolean trendingDown = avgLast3 < avgLast5 && avgLast5 < avgLast10;
        info.setTrendingUp(trendingUp);
        info.setTrendingDown(trendingDown);
        if (overStreak >= STREAK || underStreak >= STREAK) {
          hotStreaks.add(info);
        }
        if (overLast10 >= 7 || underLast10 >= 7) {
          frequentlyHit.add(info);
        }
      }
    }
    hotStreaks.sort((a, b) -> {
      Integer aStreak = a.getOverStreak() > a.getUnderStreak() ? a.getOverStreak() : a.getUnderStreak();
      Integer bStreak = b.getOverStreak() > b.getUnderStreak() ? b.getOverStreak() : b.getUnderStreak();
      return bStreak.compareTo(aStreak);
    });
    frequentlyHit.sort((a, b) -> {
      Integer aStreak = a.getOverLast10() > a.getUnderLast10() ? a.getOverLast10() : a.getUnderLast10();
      Integer bStreak = b.getOverLast10() > b.getUnderLast10() ? b.getOverLast10() : b.getUnderLast10();
      return bStreak.compareTo(aStreak);
    });
    playerProjectionInformationMap.put("Hot Streaks", hotStreaks);
    playerProjectionInformationMap.put("Frequently Hit", frequentlyHit);
  }

  private PlayerProjectionInformation mapToResponse(PrizePicksProjectionsNbaEntity entity) {
    return PlayerProjectionInformation.builder()
        .firstName(entity.getFullName().getFirstName())
        .lastName(entity.getFullName().getLastName())
        .fullName(entity.getFullName().toString())
        .lineScore(entity.getLineScore())
        .opposingTeam(entity.getOpposingTeam())
        .gameDate(entity.getGameDate())
        .build();
  }

  private List<PlayerPropsFactEntity> getLastXGamesForPlayers(List<FullName> fullNames) {
    String sqlQuery = String.format("WITH RankedPlayerProps AS (" +
        "    SELECT " +
        "        e.*, " +
        "        ROW_NUMBER() OVER (PARTITION BY e.player_id ORDER BY e.date DESC) AS RowNum " +
        "    FROM " +
        "        player_props_fact e " +
        "    WHERE " +
        "        (e.first_name, e.last_name) IN (%s) " +
        "        AND e.min > 0 " +
        ") " +
        "SELECT " +
        "    * " +
        "FROM " +
        "    RankedPlayerProps " +
        "WHERE " +
        "    RowNum <= %s", fullNamesStr(fullNames), 10);
    log.info(sqlQuery);
    List<PlayerPropsFactEntity> playerPropData = new ArrayList<>();
    try (Connection conn = dataSource.getConnection()) {
      PreparedStatement ps = conn.prepareStatement(sqlQuery);
      ps.setQueryTimeout(60);
      log.info("Executing query: {}", sqlQuery);
      ResultSet rs = ps.executeQuery();
      while (rs.next()) {
        playerPropData.add(mapResultSetToEntity(rs));
      }
    } catch (Exception e) {
      log.error("Error fetching player props data, Error: {}", e.getMessage());
    }
    return playerPropData;
  }

  private String fullNamesStr(List<FullName> fullNames) {
    return fullNames.stream()
        .map(fullName -> String.format("('%s', '%s')",
            escapeSingleQuotes(fullName.getFirstName()),
            escapeSingleQuotes(fullName.getLastName())))
        .collect(Collectors.joining(","));
  }

  private String escapeSingleQuotes(String input) {
    // Replace single quotes with two single quotes
    return input.replaceAll("'", "''");
  }

  private PlayerPropsFactEntity mapResultSetToEntity(ResultSet rs) throws SQLException {
    PlayerPropsFactEntity entity = new PlayerPropsFactEntity();
    entity.setPlayer_game_id(rs.getString("player_game_id"));
    entity.setGame_id(rs.getString("game_id"));
    entity.setDate(rs.getDate("date"));
    entity.setPlayer_id(rs.getString("player_id"));
    entity.setFullName(FullName.builder()
        .firstName(rs.getString("first_name"))
        .lastName(rs.getString("last_name"))
        .build());
    entity.setPosition(rs.getString("position"));
    entity.setTeam_id(rs.getString("team_id"));
    entity.setAbbreviation(rs.getString("abbreviation"));
    entity.setCity(rs.getString("city"));
    entity.setConference(rs.getString("conference"));
    entity.setDivision(rs.getString("division"));
    entity.setPts(rs.getInt("pts"));
    entity.setRbs(rs.getInt("rbs"));
    entity.setAsts(rs.getInt("asts"));
    entity.setStls(rs.getInt("stls"));
    entity.setBlks(rs.getInt("blks"));
    entity.setTos(rs.getInt("tos"));
    entity.setFg3m(rs.getInt("fg3m"));
    entity.setFg3a(rs.getInt("fg3a"));
    entity.setFg3_pct(rs.getDouble("fg3_pct"));
    entity.setFgm(rs.getInt("fgm"));
    entity.setFga(rs.getInt("fga"));
    entity.setFg_pct(rs.getDouble("fg_pct"));
    entity.setFtm(rs.getInt("ftm"));
    entity.setFta(rs.getInt("fta"));
    entity.setFt_pct(rs.getDouble("ft_pct"));
    entity.setOreb(rs.getInt("oreb"));
    entity.setDreb(rs.getInt("dreb"));
    entity.setPf(rs.getInt("pf"));
    entity.setMin(rs.getInt("min"));
    entity.setOpposing_team_full_name(rs.getString("opposing_team_full_name"));
    entity.setOpposing_team_id(rs.getString("opposing_team_id"));
    entity.setPostseason(rs.getBoolean("postseason"));
    entity.setSeason(rs.getInt("season"));
    return entity;
  }

  public List<LineHistory> getPlayerPropsAgainstOpponent(List<PlayerStatsResponse> data, String statType,
      Double lineScore) {
    List<PlayerPropsFactEntity> playerPropsFactEntities = data.stream()
        .map(PlayerStatsEntityToResponseMapper::responseToEntity).toList();
    List<LineHistory> lineHistories = playerPropsFactEntities.stream().map(entity -> {
      Double value = prizePicksToDbMapper.get(statType).apply(entity);
      return LineHistory.builder()
          .date(entity.getDate())
          .value(value)
          .hit(value >= lineScore)
          .build();
    }).toList();
    return lineHistories;
  }
}
