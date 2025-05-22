package com.hsbc.candidate.codingtest.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Represents the main weather parameters in the weather data.
 * This class contains information about temperature, pressure, humidity, and other
 * key meteorological measurements.
 */
@Data
@SuppressWarnings("PMD.ShortClassName") // Main is an appropriate name for this domain model
public class Main {
    /**
     * The current temperature in Kelvin.
     */
    private double temp;

    /**
     * The minimum temperature in Kelvin observed in the city at the moment.
     */
    @JsonProperty("temp_min")
    private double tempMin;

    /**
     * The maximum temperature in Kelvin observed in the city at the moment.
     */
    @JsonProperty("temp_max")
    private double tempMax;

    /**
     * The atmospheric pressure in hPa (hectopascal).
     */
    private double pressure;

    /**
     * The atmospheric pressure at sea level in hPa.
     */
    @JsonProperty("sea_level")
    private double seaLevel;

    /**
     * The atmospheric pressure at ground level in hPa.
     */
    @JsonProperty("grnd_level")
    private double groundLevel;

    /**
     * The humidity percentage, from 0 to 100.
     */
    private int humidity;
}
