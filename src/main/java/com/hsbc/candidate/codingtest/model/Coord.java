package com.hsbc.candidate.codingtest.model;

import lombok.Data;

/**
 * Represents geographical coordinates in the weather data.
 * This class contains the longitude and latitude values that define a location on Earth.
 */
@Data
public class Coord {
    /**
     * The longitude value (east/west position).
     * Positive values indicate east of the prime meridian, negative values indicate west.
     */
    private double lon;

    /**
     * The latitude value (north/south position).
     * Positive values indicate north of the equator, negative values indicate south.
     */
    private double lat;
}
