package com.player.props.util;


import static com.player.props.constants.BDLConstants.BDL_URL;

import java.util.List;

import com.player.props.model.response.SuccessfulSaveResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BdlUtil {

    public static String buildUrl(String date, int page, String attribute) {
    StringBuilder str = new StringBuilder(BDL_URL);
    str.append(attribute);
    str.append("?");
    str.append("seasons[]").append("=").append("2022");
    str.append("&per_page").append("=").append(100);

    if (date != "") {
      str.append("&start_date").append("=").append(date);
      str.append("&end_date").append("=").append(date);
    }
    if (page > 1) {
      str.append("&page=").append(page);
    }
    return str.toString();
  }

  public static void logTransaction(SuccessfulSaveResponse saveResponse, List<?> data, String attribute) {
    if (saveResponse.getRecordsSaved() == null) {
      saveResponse.setRecordsSaved(0);
    }
    saveResponse.setRecordsSaved(saveResponse.getRecordsSaved() + data.size());
    log.info("Saved {} {} Records | Saved so far {} ", data.size(), attribute, saveResponse.getRecordsSaved());
  }
}
