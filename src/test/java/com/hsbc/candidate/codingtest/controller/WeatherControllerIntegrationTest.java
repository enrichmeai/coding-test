package com.hsbc.candidate.codingtest.controller;

import com.hsbc.candidate.codingtest.model.City;
import com.hsbc.candidate.codingtest.model.WeatherResponse;
import com.hsbc.candidate.codingtest.service.WeatherService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class WeatherControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private WeatherService weatherService;

    private WeatherResponse mockWeatherResponse;

    @BeforeEach
    void setUp() {
        City alphaCity = new City();
        alphaCity.setName("AlphaCity");
        alphaCity.setId(15);

        City betaTown = new City();
        betaTown.setName("BetaTown");
        betaTown.setId(25);

        City gammaVillage = new City();
        gammaVillage.setName("GammaVillage");
        gammaVillage.setId(35);

        mockWeatherResponse = new WeatherResponse();
        mockWeatherResponse.setCod("200");
        mockWeatherResponse.setCalctime(0.25);
        mockWeatherResponse.setCnt(3);
        mockWeatherResponse.setName("TestWeatherReport");
        mockWeatherResponse.setCities(List.of(alphaCity, betaTown, gammaVillage));
    }

    @Test
    void testGetAllWeatherData() {
        when(weatherService.fetchWeatherData()).thenReturn(Mono.just(mockWeatherResponse));

        webTestClient.get()
                .uri("/api/weather")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.cod").isEqualTo("200")
                .jsonPath("$.cnt").isEqualTo(3)
                .jsonPath("$.list[0].name").isEqualTo("AlphaCity");
    }

    @Test
    void testCountCitiesStartingWith() {
        String letter = "C";
        long count = 2;
        when(weatherService.countCitiesStartingWith(letter)).thenReturn(Mono.just(count));

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/api/weather/cities/count").queryParam("letter", letter).build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.count").isEqualTo(2);
    }

    @Test
    void testGetCitiesStartingWith() {
        String letter = "C";
        List<String> cities = List.of("CityA", "CityC");
        when(weatherService.getCitiesStartingWith(letter)).thenReturn(Mono.just(cities));

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/api/weather/cities").queryParam("letter", letter).build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$[0]").isEqualTo("CityA")
                .jsonPath("$[1]").isEqualTo("CityC");
    }

}
