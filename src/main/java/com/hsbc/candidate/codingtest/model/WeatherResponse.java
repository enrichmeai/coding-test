package com.hsbc.candidate.codingtest.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

/**
 * Represents the top-level response from the OpenWeather API.
 * This class contains metadata about the response and a list of cities with their weather data.
 */
@Data
public class WeatherResponse {
    /**
     * The internal parameter of the response (success or failure code).
     * Usually "200" for successful responses.
     */
    private String cod;

    /**
     * The calculation time of the data in seconds.
     * Indicates how long it took to generate the response.
     */
    private double calctime;

    /**
     * The number of cities returned in the response.
     */
    private int cnt;

    /**
     * The name of the response (usually empty or contains the region name).
     */
    private String name;

    /**
     * The list of cities with their weather data.
     * This field is mapped from the JSON property "list" in the API response.
     */
    @JsonProperty("list")
    private List<City> cities;
}
