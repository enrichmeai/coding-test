package com.hsbc.candidate.codingtest.model;

import lombok.Data;

/**
 * Represents wind information in the weather data.
 * This class contains data about wind speed and direction.
 */
@Data
@SuppressWarnings("PMD.ShortClassName") // Wind is an appropriate name for this domain model
public class Wind {
    /**
     * The wind speed in meters per second.
     */
    private double speed;

    /**
     * The wind direction in degrees (meteorological).
     * 0 degrees means wind from the north, 90 degrees means wind from the east, etc.
     */
    private double deg;
}
