package com.hsbc.candidate.codingtest.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class WeatherServiceWebclientConfig {

    @Value("${weather.api.base-url}")
    private String apiBaseUrl;

    @Value("${weather.api.appid}")
    private String apiAppId;

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

    @Bean(name = "openWeatherApiUrl")
    public String weatherApiUrl() {
        String bboxParam = String.format("?bbox=%s", bboxConfig.formatBboxParam());
        return apiBaseUrl + bboxParam + "&appid=" + apiAppId;
    }
}
