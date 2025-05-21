package com.hsbc.candidate.codingtest.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Main {
    private double temp;

    @JsonProperty("temp_min")
    private double tempMin;

    @JsonProperty("temp_max")
    private double tempMax;

    private double pressure;

    @JsonProperty("sea_level")
    private double seaLevel;

    @JsonProperty("grnd_level")
    private double groundLevel;

    private int humidity;
}
