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

    @Value("${weather.api.bbox.lon-left}")
    private double lonLeft;

    @Value("${weather.api.bbox.lat-bottom}")
    private double latBottom;

    @Value("${weather.api.bbox.lon-right}")
    private double lonRight;

    @Value("${weather.api.bbox.lat-top}")
    private double latTop;

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
