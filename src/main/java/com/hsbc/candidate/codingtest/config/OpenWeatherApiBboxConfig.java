package com.hsbc.candidate.codingtest.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import lombok.Getter;

/**
 * Configuration class for OpenWeather API bounding box parameters.
 * This class encapsulates all the parameters needed to define the geographical area
 * for which weather data is requested.
 */
@Configuration
@Getter
public class OpenWeatherApiBboxConfig {

    /**
     * The longitude of the left (westernmost) edge of the bounding box.
     * This value is injected from the application properties.
     */
    @Value("${weather.api.bbox.lon-left}")
    private double lonLeft;

    /**
     * The latitude of the bottom (southernmost) edge of the bounding box.
     * This value is injected from the application properties.
     */
    @Value("${weather.api.bbox.lat-bottom}")
    private double latBottom;

    /**
     * The longitude of the right (easternmost) edge of the bounding box.
     * This value is injected from the application properties.
     */
    @Value("${weather.api.bbox.lon-right}")
    private double lonRight;

    /**
     * The latitude of the top (northernmost) edge of the bounding box.
     * This value is injected from the application properties.
     */
    @Value("${weather.api.bbox.lat-top}")
    private double latTop;

    /**
     * The zoom level for the map view.
     * This affects the number of cities returned in the API response.
     * This value is injected from the application properties.
     */
    @Value("${weather.api.bbox.zoom}")
    private int zoom;

    /**
     * Formats the bounding box parameters as a string for use in the API URL.
     *
     * @return A string in the format "lon-left,lat-bottom,lon-right,lat-top,zoom"
     */
    public String formatBboxParam() {
        return String.format("%s,%s,%s,%s,%s",
                lonLeft, latBottom, lonRight, latTop, zoom);
    }
}
