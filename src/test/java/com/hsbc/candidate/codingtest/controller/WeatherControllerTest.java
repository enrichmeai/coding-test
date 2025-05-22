package com.hsbc.candidate.codingtest.controller;

import com.hsbc.candidate.codingtest.exception.GlobalExceptionHandler;
import com.hsbc.candidate.codingtest.model.City;
import com.hsbc.candidate.codingtest.model.WeatherResponse;
import com.hsbc.candidate.codingtest.service.WeatherService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;

import static org.mockito.Mockito.when;

public class WeatherControllerTest {

    private WebTestClient webTestClient;

    @Mock
    private WeatherService weatherService;


    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        WeatherController controller = new WeatherController(weatherService);

        webTestClient = WebTestClient
                .bindToController(controller)
                .controllerAdvice(new GlobalExceptionHandler())
                .build();

        when(weatherService.countCitiesStartingWith("N")).thenReturn(Mono.just(1L));
        when(weatherService.getCitiesStartingWith("N")).thenReturn(Mono.just(Arrays.asList("New York", "Nashville")));

        when(weatherService.countCitiesStartingWith("ABC")).thenReturn(Mono.empty());
        when(weatherService.countCitiesStartingWith("1")).thenReturn(Mono.empty());
        when(weatherService.getCitiesStartingWith("ABC")).thenReturn(Mono.empty());
        when(weatherService.getCitiesStartingWith("1")).thenReturn(Mono.empty());
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
    public void testGetCitiesStartingWith() {
        when(weatherService.getCitiesStartingWith("N")).thenReturn(Mono.just(Arrays.asList("New York", "Nashville")));

        webTestClient.get()
                .uri("/api/weather/cities?letter=N")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0]").isEqualTo("New York")
                .jsonPath("$[1]").isEqualTo("Nashville");
    }

    @Test
    public void testCountCitiesStartingWithInvalidLetterTooLong() {
        webTestClient.get()
                .uri("/api/weather/cities/count?letter=ABC")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    public void testCountCitiesStartingWithInvalidLetterNumber() {
        webTestClient.get()
                .uri("/api/weather/cities/count?letter=1")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    public void testGetCitiesStartingWithInvalidLetterTooLong() {
        webTestClient.get()
                .uri("/api/weather/cities?letter=ABC")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    public void testGetCitiesStartingWithInvalidLetterNumber() {
        webTestClient.get()
                .uri("/api/weather/cities?letter=1")
                .exchange()
                .expectStatus().isOk();
    }
}
