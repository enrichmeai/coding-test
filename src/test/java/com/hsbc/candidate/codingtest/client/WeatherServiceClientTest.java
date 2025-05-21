package com.hsbc.candidate.codingtest.client;

import com.hsbc.candidate.codingtest.exception.CityLetterFinderSystemException;
import com.hsbc.candidate.codingtest.exception.WeatherServiceException;
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

@ExtendWith(MockitoExtension.class)
class WeatherServiceClientTest {

    @Mock
    private WebClient.Builder webClientBuilder;

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @InjectMocks
    private WeatherServiceClient weatherServiceClient;

    @BeforeEach
    void setUp() {
        lenient().when(webClientBuilder.build()).thenReturn(webClient);
        lenient().when(webClient.get()).thenReturn(requestHeadersUriSpec);
        lenient().when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        lenient().when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);

        weatherServiceClient = new WeatherServiceClient(webClient, "https://test-api-url");
    }

    @Test
    void fetchWeatherData_shouldReturnWeatherResponse() {
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

    @Test
    void fetchWeatherData_shouldWrapWebClientResponseExceptionAsWeatherServiceException() {
        // Given
        WebClientResponseException exception = new WebClientResponseException(
                500, "Internal Server Error", null, null, null);

        when(responseSpec.bodyToMono(WeatherResponse.class)).thenReturn(Mono.error(exception));

        // When & Then
        StepVerifier.create(weatherServiceClient.fetchWeatherData())
                .expectErrorMatches(throwable -> throwable instanceof WeatherServiceException &&
                        ((WeatherServiceException) throwable).getErrorCode().equals(WeatherServiceException.SERVICE_UNAVAILABLE))
                .verify();
    }

    @Test
    void fetchWeatherData_shouldWrapUnknownErrorsAsCityLetterFinderSystemException() {
        // Given
        RuntimeException unknownException = new RuntimeException("Unknown error");

        when(responseSpec.bodyToMono(WeatherResponse.class)).thenReturn(Mono.error(unknownException));

        // When & Then
        StepVerifier.create(weatherServiceClient.fetchWeatherData())
                .expectErrorMatches(throwable -> throwable instanceof CityLetterFinderSystemException &&
                        ((CityLetterFinderSystemException) throwable).getErrorCode().equals(WeatherServiceException.DATA_PROCESSING_ERROR))
                .verify();
    }
}
