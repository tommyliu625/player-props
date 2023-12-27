package com.player.props.util;

import java.sql.Date;
import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtil {

  public static String iterateDate(String date) throws ParseException {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    LocalDate dt = LocalDate.parse(date, formatter);
    LocalDate nextDay = dt.plusDays(1);
    String nextDayString = nextDay.format(formatter);
    return nextDayString;
  }

  public static boolean checkIsCurrentDate(String date) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    String today = LocalDate.now().format(formatter);
    return today.equals(date);
  }

  public static String yesterdayStr() {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    String yesterday = LocalDate.now().minusDays(1).format(formatter);
    return yesterday;
  }

  public static Instant underdogLastUpdated = Instant.now();

  public static Instant prizePickLastUpdated = Instant.now();

  static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

  public static String formatInstant(Instant instant) {
    ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(instant, ZoneId.systemDefault());
    return formatter.format(zonedDateTime);
  }
  
}
