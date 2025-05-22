package com.hsbc.candidate.codingtest.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Represents rainfall information in the weather data.
 * This class contains data about precipitation volume over different time periods.
 */
@Data
@SuppressWarnings("PMD.ShortClassName") // Rain is an appropriate name for this domain model
public class Rain {
    /**
     * The rainfall volume for the last 3 hours, in millimeters.
     * This field is mapped from the JSON property "3h" in the API response.
     */
    @JsonProperty("3h")
    private double threeHour;
}
