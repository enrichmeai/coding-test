package com.hsbc.candidate.codingtest.model;

import lombok.Data;

/**
 * Represents a weather condition in the weather data.
 * This class contains information about a specific weather condition such as
 * "clear sky", "few clouds", "rain", etc., along with its description and icon.
 */
@Data
@SuppressWarnings("PMD.ShortClassName") // Weather is an appropriate name for this domain model
public class Weather {
    /**
     * The unique identifier for the weather condition.
     * Each weather condition has a specific ID in the OpenWeather API.
     */
    private int id;

    /**
     * The main weather parameter (Rain, Snow, Clouds, etc.).
     * This is a short name for the weather condition.
     */
    private String main;

    /**
     * The weather condition description within the group.
     * This provides more detailed information about the weather condition.
     */
    private String description;

    /**
     * The weather icon ID for visual representation.
     * This can be used to fetch the corresponding weather icon from the API.
     */
    private String icon;
}
