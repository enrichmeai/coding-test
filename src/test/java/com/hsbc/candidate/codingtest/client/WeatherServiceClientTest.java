package com.hsbc.candidate.codingtest.client;

import com.hsbc.candidate.codingtest.model.WeatherResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

/**
 * Unit tests for the WeatherServiceClient class.
 * These tests verify that the client correctly interacts with the WebClient
 * to fetch weather data and handles errors appropriately.
 */
@ExtendWith(MockitoExtension.class)
@SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert") // Tests use StepVerifier for reactive stream assertions
class WeatherServiceClientTest {

    /**
     * Mock WebClient.Builder used to create the WebClient.
     */
    @Mock
    private WebClient.Builder webClientBuilder;

    /**
     * Mock WebClient used to make HTTP requests.
     */
    @Mock
    private WebClient webClient;

    /**
     * Mock request headers URI specification for configuring the request URI.
     */
    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    /**
     * Mock request headers specification for configuring request headers.
     */
    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    /**
     * Mock response specification for handling the response.
     */
    @Mock
    private WebClient.ResponseSpec responseSpec;

    /**
     * The WeatherServiceClient instance being tested.
     */
    @InjectMocks
    private WeatherServiceClient weatherServiceClient;

    /**
     * Sets up the test environment before each test.
     * Configures the mock WebClient chain and creates a new WeatherServiceClient instance.
     */
    @BeforeEach
    void setUp() {
        lenient().when(webClientBuilder.build()).thenReturn(webClient);
        lenient().when(webClient.get()).thenReturn(requestHeadersUriSpec);
        lenient().when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        lenient().when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);

        weatherServiceClient = new WeatherServiceClient(webClient, "https://test-api-url");
    }

    /**
     * Tests that fetchWeatherData successfully returns a WeatherResponse when the API call succeeds.
     * This test uses StepVerifier for reactive stream assertions instead of traditional JUnit assertions.
     */
    @Test
    void fetchWeatherDataShouldReturnWeatherResponse() {
        // Given
        WeatherResponse mockResponse = new WeatherResponse();
        mockResponse.setCod("200");
        mockResponse.setCalctime(0.1234);
        mockResponse.setCnt(2);

        when(responseSpec.bodyToMono(WeatherResponse.class)).thenReturn(Mono.just(mockResponse));

        // When & Then
        StepVerifier.create(weatherServiceClient.fetchWeatherData())
                .expectNext(mockResponse)
                .verifyComplete();
    }

    /**
     * Tests that fetchWeatherData properly propagates WebClientResponseException when the API call fails.
     * This test uses StepVerifier for reactive stream assertions instead of traditional JUnit assertions.
     */
    @Test
    void fetchWeatherDataShouldPropagateWebClientResponseException() {
        // Given
        WebClientResponseException exception = new WebClientResponseException(
                500, "Internal Server Error", null, null, null);

        when(responseSpec.bodyToMono(WeatherResponse.class)).thenReturn(Mono.error(exception));

        // When & Then
        StepVerifier.create(weatherServiceClient.fetchWeatherData())
                .expectError(WebClientResponseException.class)
                .verify();
    }

    /**
     * Tests that fetchWeatherData properly propagates unknown errors when they occur.
     * This test uses StepVerifier for reactive stream assertions instead of traditional JUnit assertions.
     */
    @Test
    void fetchWeatherDataShouldPropagateUnknownErrors() {
        // Given
        RuntimeException unknownException = new RuntimeException("Unknown error");

        when(responseSpec.bodyToMono(WeatherResponse.class)).thenReturn(Mono.error(unknownException));

        // When & Then
        StepVerifier.create(weatherServiceClient.fetchWeatherData())
                .expectError(RuntimeException.class)
                .verify();
    }
}
