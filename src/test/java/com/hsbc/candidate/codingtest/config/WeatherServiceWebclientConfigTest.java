package com.hsbc.candidate.codingtest.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.reactive.function.client.WebClient;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@SpringBootTest
class WeatherServiceWebclientConfigTest {

    @MockitoBean
    private OpenWeatherApiBboxConfig bboxConfig;

    @Autowired
    private WeatherServiceWebclientConfig weatherServiceWebclientConfig;

    @Value("${weather.api.base-url}")
    private String apiBaseUrl;

    @Value("${weather.api.appid}")
    private String apiAppId;

    @Test
    void testWeatherApiUrlWithValidConfiguration() {
        // Setup
        String mockBboxParam = "12,34,56,78,10";
        when(bboxConfig.formatBboxParam()).thenReturn(mockBboxParam);

        // Execute
        String actualUrl = weatherServiceWebclientConfig.weatherApiUrl();

        // Verify
        assertTrue(actualUrl.startsWith(apiBaseUrl), "URL should start with the base URL");
        assertTrue(actualUrl.contains("?bbox="), "URL should contain the bbox parameter");
        assertTrue(actualUrl.contains("&appid=" + apiAppId), "URL should contain the API key");
    }

    @Test
    void testWeatherServiceWebClientBeanCreation() {
        WebClient.Builder webClientBuilder = WebClient.builder();
        WebClient webClient = weatherServiceWebclientConfig.weatherServiceWebClient(webClientBuilder);
        assertInstanceOf(WebClient.class, webClient, "The returned object should be an instance of WebClient");
    }
}
