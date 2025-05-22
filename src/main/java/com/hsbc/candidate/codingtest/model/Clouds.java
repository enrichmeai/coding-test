package com.hsbc.candidate.codingtest.model;

import lombok.Data;

/**
 * Represents cloud coverage information in the weather data.
 * This class contains data about the cloudiness percentage.
 */
@Data
public class Clouds {
    /**
     * The cloudiness percentage, from 0 to 100.
     * A value of 0 means clear sky, while 100 means completely cloudy.
     */
    private int all;
}
