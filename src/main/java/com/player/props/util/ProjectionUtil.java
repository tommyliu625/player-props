package com.player.props.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import com.player.props.dao.PlayerPropsFactEntity;

public class ProjectionUtil {
        public static Map<String, Function<PlayerPropsFactEntity, Double>> prizePicksToDbMapper = new HashMap<>();
        static {
                prizePicksToDbMapper.put("Points", entity -> (double) entity.getPts());
                prizePicksToDbMapper.put("Rebounds", entity -> (double) entity.getRbs());
                prizePicksToDbMapper.put("Defensive Rebounds", entity -> (double) entity.getDreb());
                prizePicksToDbMapper.put("Offensive Rebounds", entity -> (double) entity.getOreb());
                prizePicksToDbMapper.put("Assists", entity -> (double) entity.getAsts());
                prizePicksToDbMapper.put("Blocked Shots", entity -> (double) entity.getBlks());
                prizePicksToDbMapper.put("Steals", entity -> (double) entity.getStls());
                prizePicksToDbMapper.put("Turnovers", entity -> (double) entity.getTos());
                prizePicksToDbMapper.put("3-PT Made", entity -> (double) entity.getFg3m());
                prizePicksToDbMapper.put("Free Throws Made", entity -> (double) entity.getFtm());
                prizePicksToDbMapper.put("3-PT Attempted", entity -> (double) entity.getFg3a());
                prizePicksToDbMapper.put("FG Attempted", entity -> (double) entity.getFga());
                prizePicksToDbMapper.put("Pts+Asts", entity -> (double) (entity.getPts() + entity.getAsts()));
                prizePicksToDbMapper.put("Rebs+Asts", entity -> (double) (entity.getAsts() + entity.getRbs()));
                prizePicksToDbMapper.put("Pts+Rebs", entity -> (double) (entity.getPts() + entity.getRbs()));
                prizePicksToDbMapper.put("Pts+Rebs+Asts",
                                entity -> (double) (entity.getPts() + entity.getRbs() + entity.getAsts()));
                prizePicksToDbMapper.put("Blks+Stls", entity -> (double) (entity.getStls() + entity.getBlks()));
                prizePicksToDbMapper.put("Fantasy Score",
                                entity -> (double) entity.getPts() + (double) entity.getRbs() * 1.2 +
                                                (double) entity.getAsts() * 1.5 + (double) entity.getStls() * 3
                                                + (double) entity.getBlks() * 3
                                                - (double) entity.getTos());
                prizePicksToDbMapper.put("Minutes Played", entity -> (double) entity.getMin());
        }
        public static Map<String, Function<List<PlayerPropsFactEntity>, Double>> comboToDbMapper = new HashMap<>();
        static {
                comboToDbMapper.put("3-PT Made (Combo)",
                                entities -> (double) entities.get(0).getFg3m() + (double) entities.get(1).getFg3m());
                comboToDbMapper.put("Assists (Combo)",
                                entities -> (double) entities.get(0).getAsts() + (double) entities.get(1).getAsts());
                comboToDbMapper.put("Points (Combo)",
                                entities -> (double) entities.get(0).getPts() + (double) entities.get(1).getPts());
                comboToDbMapper.put("Rebounds (Combo)",
                                entities -> (double) entities.get(0).getRbs() + (double) entities.get(1).getRbs());
        }

        public static Map<String, Function<PlayerPropsFactEntity, Double>> underdogToDbMapper = new HashMap<>();
        static {
                // underdogToDbMapper.put("Double Doubles", entity -> (double) entity.getDd());
                underdogToDbMapper.put("Blocks", entity -> (double) entity.getBlks());
                underdogToDbMapper.put("Steals", entity -> (double) entity.getStls());
                underdogToDbMapper.put("Blocks + Steals", entity -> (double) (entity.getBlks() + entity.getStls()));
                underdogToDbMapper.put("3-Pointers Made", entity -> (double) entity.getFg3m());
                underdogToDbMapper.put("Points", entity -> (double) entity.getPts());
                underdogToDbMapper.put("Pts + Rebs + Asts",
                                entity -> (double) (entity.getPts() + entity.getRbs() + entity.getAsts()));
                underdogToDbMapper.put("Assists", entity -> (double) entity.getAsts());
                underdogToDbMapper.put("Rebounds", entity -> (double) entity.getRbs());
                underdogToDbMapper.put("Rebounds + Assists", entity -> (double) (entity.getRbs() + entity.getAsts()));
                underdogToDbMapper.put("Points + Rebounds", entity -> (double) (entity.getPts() + entity.getRbs()));
                underdogToDbMapper.put("Points + Assists", entity -> (double) (entity.getPts() + entity.getAsts()));
                underdogToDbMapper.put("Fantasy Points", entity -> (double) (entity.getPts() + entity.getRbs() * 1.2
                                + entity.getAsts() * 1.5 + entity.getStls() * 3 + entity.getBlks() * 3
                                - entity.getTos()));
                underdogToDbMapper.put("Turnovers", entity -> (double) entity.getTos());
                underdogToDbMapper.put("FT Made", entity -> (double) entity.getFtm());
                // underdogToDbMapper.put("Triple Doubles", entity -> (double) entity.getTd());
        }

        public static double calculatePercentage(double avg, double lineScore) {
                return (avg / lineScore - 1) * 100;
        }

}
