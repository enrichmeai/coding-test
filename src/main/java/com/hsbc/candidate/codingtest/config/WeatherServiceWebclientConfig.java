package com.hsbc.candidate.codingtest.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import lombok.RequiredArgsConstructor;

/**
 * Configuration class for the Weather Service WebClient.
 * This class provides beans for the WebClient used to communicate with the OpenWeather API
 * and the URL for the API endpoint.
 */
@Configuration
@RequiredArgsConstructor
public class WeatherServiceWebclientConfig {

    /**
     * Base URL for the OpenWeather API.
     * Injected from application properties.
     */
    @Value("${weather.api.base-url}")
    private String apiBaseUrl;

    /**
     * API key (appid) for the OpenWeather API.
     * Injected from application properties.
     */
    @Value("${weather.api.appid}")
    private String apiAppId;

    /**
     * Configuration for the bounding box parameters used in the OpenWeather API.
     */
    private final OpenWeatherApiBboxConfig bboxConfig;

    /**
     * Creates a {@link WebClient} bean for interacting with the OpenWeather API.
     *
     * @param webClientBuilder the builder used to configure and construct the {@link WebClient}
     * @return a configured {@link WebClient} instance
     */
    @Bean(name = "openWeatherApiWebClient")
    public WebClient weatherServiceWebClient(WebClient.Builder webClientBuilder) {
        return webClientBuilder.build();
    }

    /**
     * Creates a bean with the complete URL for the OpenWeather API.
     * The URL includes the base URL, bounding box parameters, and API key.
     *
     * @return the complete URL for the OpenWeather API
     */
    @Bean(name = "openWeatherApiUrl")
    public String weatherApiUrl() {
        String bboxParam = String.format("?bbox=%s", bboxConfig.formatBboxParam());
        return apiBaseUrl + bboxParam + "&appid=" + apiAppId;
    }
}
