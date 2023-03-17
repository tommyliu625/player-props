package com.player.props.util;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateUtil {

  public static String iterateDate(String date) throws ParseException {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    LocalDate dt = LocalDate.parse(date, formatter);
    LocalDate nextDay = dt.plusDays(1);
    String nextDayString = nextDay.format(formatter);
    return nextDayString;
  }
}
