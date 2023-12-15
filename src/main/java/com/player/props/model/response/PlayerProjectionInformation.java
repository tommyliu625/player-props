package com.player.props.model.response;

import java.sql.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PlayerProjectionInformation {
    public String playerId;
    public String statType;
    public String firstName;
    public String lastName;
    public String fullName;
    public Double lineScore;
    public String opposingTeam;
    public Date gameDate;
    public Integer overStreak;
    public Integer overLast3;
    public Integer overLast5;
    public Integer overLast10;
    public Integer underStreak;
    public Integer underLast3;
    public Integer underLast5;
    public Integer underLast10;
    public String avgLast10;
    public String avgLast5;
    public String avgLast3;
    public boolean trendingUp;
    public boolean trendingDown;
    public List<LineHistory> lineHistory;


    // Getters and setters

}