package com.hsbc.candidate.codingtest.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@TestPropertySource(properties = {
        "weather.api.base-url=https://api.weather.com",
        "weather.api.appid=testAppId",
        "weather.api.bbox.lon-left=-10.0",
        "weather.api.bbox.lat-bottom=35.0",
        "weather.api.bbox.lon-right=-5.0",
        "weather.api.bbox.lat-top=40.0",
        "weather.api.bbox.zoom=10"
})
class WeatherServiceWebclientConfigTest {

    @Value("${weather.api.base-url}")
    private String apiBaseUrl;

    @Value("${weather.api.appid}")
    private String apiAppId;

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

    @Test
    void testWeatherApiUrl() {
        // Create and set up the OpenWeatherApiBboxConfig
        OpenWeatherApiBboxConfig bboxConfig = new OpenWeatherApiBboxConfig();

        // Set the bbox properties using reflection
        try {
            java.lang.reflect.Field lonLeftField = OpenWeatherApiBboxConfig.class.getDeclaredField("lonLeft");
            lonLeftField.setAccessible(true);
            lonLeftField.set(bboxConfig, lonLeft);

            java.lang.reflect.Field latBottomField = OpenWeatherApiBboxConfig.class.getDeclaredField("latBottom");
            latBottomField.setAccessible(true);
            latBottomField.set(bboxConfig, latBottom);

            java.lang.reflect.Field lonRightField = OpenWeatherApiBboxConfig.class.getDeclaredField("lonRight");
            lonRightField.setAccessible(true);
            lonRightField.set(bboxConfig, lonRight);

            java.lang.reflect.Field latTopField = OpenWeatherApiBboxConfig.class.getDeclaredField("latTop");
            latTopField.setAccessible(true);
            latTopField.set(bboxConfig, latTop);

            java.lang.reflect.Field zoomField = OpenWeatherApiBboxConfig.class.getDeclaredField("zoom");
            zoomField.setAccessible(true);
            zoomField.set(bboxConfig, zoom);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set bbox fields via reflection", e);
        }

        // Create the WeatherServiceWebclientConfig with the bboxConfig
        WeatherServiceWebclientConfig config = new WeatherServiceWebclientConfig(bboxConfig);

        // Set the remaining properties using reflection
        try {
            java.lang.reflect.Field apiBaseUrlField = WeatherServiceWebclientConfig.class.getDeclaredField("apiBaseUrl");
            apiBaseUrlField.setAccessible(true);
            apiBaseUrlField.set(config, apiBaseUrl);

            java.lang.reflect.Field apiAppIdField = WeatherServiceWebclientConfig.class.getDeclaredField("apiAppId");
            apiAppIdField.setAccessible(true);
            apiAppIdField.set(config, apiAppId);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set config fields via reflection", e);
        }

        // Format the expected URL using the same format as in the config class
        String bboxParamString = String.format("%s,%s,%s,%s,%s",
                lonLeft, latBottom, lonRight, latTop, zoom);
        String expectedUrl = String.format("%s?bbox=%s&appid=%s",
                apiBaseUrl, bboxParamString, apiAppId);

        assertEquals(expectedUrl, config.weatherApiUrl());
    }
}
