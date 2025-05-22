package com.hsbc.candidate.codingtest.model;

import lombok.Data;
import java.util.List;

/**
 * Represents a city in the weather data response.
 * This class contains information about a city including its geographical coordinates,
 * current weather conditions, and other meteorological data.
 */
@Data
@SuppressWarnings("PMD.ShortClassName") // City is an appropriate name for this domain model
public class City {
    /**
     * The unique identifier for the city.
     */
    private long id;

    /**
     * The name of the city.
     */
    private String name;

    /**
     * The geographical coordinates (latitude and longitude) of the city.
     */
    private Coord coord;

    /**
     * The main weather parameters such as temperature, pressure, and humidity.
     */
    private Main main;

    /**
     * The time of data calculation, unix, UTC.
     */
    private long dt;

    /**
     * The wind conditions including speed and direction.
     */
    private Wind wind;

    /**
     * The rainfall information if available.
     */
    private Rain rain;

    /**
     * The cloud coverage information.
     */
    private Clouds clouds;

    /**
     * The list of weather conditions (can include multiple conditions like "cloudy" and "rainy").
     */
    private List<Weather> weather;
}
