package com.hsbc.candidate.codingtest.controller;

import com.hsbc.candidate.codingtest.exception.GlobalExceptionHandler;
import com.hsbc.candidate.codingtest.exception.WeatherServiceException;
import com.hsbc.candidate.codingtest.model.City;
import com.hsbc.candidate.codingtest.model.WeatherResponse;
import com.hsbc.candidate.codingtest.service.WeatherService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;

public class WeatherControllerTest {

    private WebTestClient webTestClient;

    @Mock
    private WeatherService weatherService;

    private WeatherController weatherController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        weatherController = new WeatherController(weatherService);
        webTestClient = WebTestClient
                .bindToController(weatherController)
                .controllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    public void testGetAllWeatherData() {
        WeatherResponse response = new WeatherResponse();
        response.setCod("200");
        response.setCalctime(0.1234);
        response.setCnt(2);
        response.setName("Test Weather Data");

        City city1 = new City();
        city1.setId(1L);
        city1.setName("New York");

        City city2 = new City();
        city2.setId(2L);
        city2.setName("London");

        response.setCities(Arrays.asList(city1, city2));

        when(weatherService.fetchWeatherData()).thenReturn(Mono.just(response));

        StepVerifier.create(weatherService.fetchWeatherData())
                .expectNext(response)
                .verifyComplete();

        webTestClient.get()
                .uri("/api/weather")
                .exchange()
                .expectStatus().isOk()
                .expectBody(WeatherResponse.class)
                .isEqualTo(response);
    }

    @Test
    public void testGetAllWeatherData_ServiceException() {
        WeatherServiceException exception = new WeatherServiceException(
                WeatherServiceException.SERVICE_UNAVAILABLE,
                "External weather service is unavailable."
        );
        when(weatherService.fetchWeatherData()).thenReturn(Mono.error(exception));

        StepVerifier.create(weatherService.fetchWeatherData())
                .expectErrorMatches(err -> err instanceof WeatherServiceException &&
                        ((WeatherServiceException) err).getErrorCode().equals(WeatherServiceException.SERVICE_UNAVAILABLE) &&
                        err.getMessage().equals("External weather service is unavailable."))
                .verify();

        webTestClient.get()
                .uri("/api/weather")
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.SERVICE_UNAVAILABLE)
                .expectBody()
                .jsonPath("$.message").isEqualTo("External weather service is unavailable.");
    }

    @Test
    public void testCountCitiesStartingWith() {
        when(weatherService.countCitiesStartingWith("N")).thenReturn(Mono.just(1L));

        StepVerifier.create(weatherService.countCitiesStartingWith("N"))
                .expectNext(1L)
                .verifyComplete();

        webTestClient.get()
                .uri("/api/weather/cities/count?letter=N")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.count").isEqualTo(1);
    }

    @Test
    public void testGetCitiesStartingWith_InvalidData() {
        WeatherServiceException exception = new WeatherServiceException(
                WeatherServiceException.INVALID_DATA,
                "Weather response contains no cities data"
        );
        when(weatherService.getCitiesStartingWith("Z")).thenReturn(Mono.error(exception));

        StepVerifier.create(weatherService.getCitiesStartingWith("Z"))
                .expectErrorMatches(err -> err instanceof WeatherServiceException &&
                        ((WeatherServiceException) err).getErrorCode().equals(WeatherServiceException.INVALID_DATA) &&
                        err.getMessage().equals("Weather response contains no cities data"))
                .verify();

        webTestClient.get()
                .uri("/api/weather/cities?letter=Z")
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.BAD_REQUEST)
                .expectBody()
                .jsonPath("$.message").isEqualTo("Weather response contains no cities data");
    }
}
