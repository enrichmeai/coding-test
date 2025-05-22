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

/**
 * Integration tests for the WeatherServiceWebclientConfig class.
 * These tests verify that the configuration correctly builds the WebClient
 * and constructs the proper API URL with the required parameters.
 */
@SpringBootTest
class WeatherServiceWebclientConfigTest {

    /**
     * Mock of the OpenWeatherApiBboxConfig used to provide bounding box parameters.
     * This is mocked to control the formatBboxParam() method's return value.
     */
    @MockitoBean
    private OpenWeatherApiBboxConfig bboxConfig;

    /**
     * The WeatherServiceWebclientConfig instance being tested.
     * This is autowired by Spring to inject the actual bean from the application context.
     */
    @Autowired
    private WeatherServiceWebclientConfig weatherServiceWebclientConfig;

    /**
     * The base URL for the OpenWeather API.
     * This value is injected from the application properties.
     */
    @Value("${weather.api.base-url}")
    private String apiBaseUrl;

    /**
     * The API key (appid) for the OpenWeather API.
     * This value is injected from the application properties.
     */
    @Value("${weather.api.appid}")
    private String apiAppId;

    /**
     * Tests that the weatherApiUrl method correctly constructs the API URL
     * with the base URL, bounding box parameters, and API key.
     */
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

    /**
     * Tests that the weatherServiceWebClient method correctly creates a WebClient instance
     * from the provided WebClient.Builder.
     */
    @Test
    void testWeatherServiceWebClientBeanCreation() {
        WebClient.Builder webClientBuilder = WebClient.builder();
        WebClient webClient = weatherServiceWebclientConfig.weatherServiceWebClient(webClientBuilder);
        assertInstanceOf(WebClient.class, webClient, "The returned object should be an instance of WebClient");
    }
}
