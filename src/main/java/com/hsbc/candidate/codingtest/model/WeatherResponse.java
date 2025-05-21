package com.hsbc.candidate.codingtest.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
public class WeatherResponse {
    private String cod;
    private double calctime;
    private int cnt;
    private String name;

    @JsonProperty("list")
    private List<City> cities;
}
