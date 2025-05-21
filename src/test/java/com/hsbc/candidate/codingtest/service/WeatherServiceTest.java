package com.hsbc.candidate.codingtest.service;

import com.hsbc.candidate.codingtest.client.WeatherServiceClient;
import com.hsbc.candidate.codingtest.exception.WeatherServiceException;
import com.hsbc.candidate.codingtest.model.City;
import com.hsbc.candidate.codingtest.model.WeatherResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WeatherServiceTest {

    @Test
    void fetchWeatherDataShouldReturnWeatherResponseWhenSuccessful() {
        WeatherResponse mockResponse = new WeatherResponse();

        when(weatherServiceClient.fetchWeatherData()).thenReturn(Mono.just(mockResponse));

        StepVerifier.create(weatherService.fetchWeatherData())
                .expectNext(mockResponse)
                .verifyComplete();
    }

    @Test
    void fetchWeatherDataShouldHandleWeatherServiceException() {
        when(weatherServiceClient.fetchWeatherData()).thenReturn(Mono.error(new RuntimeException("Unexpected Error")));

        StepVerifier.create(weatherService.fetchWeatherData())
                .expectErrorMatches(throwable -> throwable instanceof WeatherServiceException &&
                        "Error processing weather data in service layer".equals(throwable.getMessage()))
                .verify();
    }

    @Test
    void fetchWeatherDataShouldHandleNullResponse() {
        when(weatherServiceClient.fetchWeatherData()).thenReturn(Mono.empty());

        StepVerifier.create(weatherService.fetchWeatherData())
                .expectComplete()
                .verify();
    }

    @Mock
    private WeatherServiceClient weatherServiceClient;

    @InjectMocks
    private WeatherService weatherService;

    @Test
    void countCitiesStartingWithShouldReturnCorrectCount() {
        WeatherResponse mockResponse = new WeatherResponse();
        List<City> cities = Arrays.asList(
                createCity("Zuwarah"),
                createCity("Zawiya"),
                createCity("Zlitan"),
                createCity("Yafran"),
                createCity("Tripoli")
        );
        mockResponse.setCities(cities);

        when(weatherServiceClient.fetchWeatherData()).thenReturn(Mono.just(mockResponse));

        StepVerifier.create(weatherService.countCitiesStartingWith("z"))
                .expectNext(3L)
                .verifyComplete();

        StepVerifier.create(weatherService.countCitiesStartingWith("y"))
                .expectNext(1L)
                .verifyComplete();

        StepVerifier.create(weatherService.countCitiesStartingWith("x"))
                .expectNext(0L)
                .verifyComplete();
    }

    @Test
    void getCitiesStartingWithShouldReturnCorrectCities() {
        WeatherResponse mockResponse = new WeatherResponse();
        List<City> cities = Arrays.asList(
                createCity("Zuwarah"),
                createCity("Zawiya"),
                createCity("Zlitan"),
                createCity("Yafran"),
                createCity("Tripoli")
        );
        mockResponse.setCities(cities);

        when(weatherServiceClient.fetchWeatherData()).thenReturn(Mono.just(mockResponse));

        StepVerifier.create(weatherService.getCitiesStartingWith("z"))
                .expectNext(Arrays.asList("Zuwarah", "Zawiya", "Zlitan"))
                .verifyComplete();

        StepVerifier.create(weatherService.getCitiesStartingWith("y"))
                .expectNext(List.of("Yafran"))
                .verifyComplete();

        StepVerifier.create(weatherService.getCitiesStartingWith("x"))
                .expectNext(List.of())
                .verifyComplete();
    }

    @Test
    void countCitiesStartingWithShouldHandleNullOrEmptyInput() {
        StepVerifier.create(weatherService.countCitiesStartingWith(null))
                .expectNext(0L)
                .verifyComplete();

        StepVerifier.create(weatherService.countCitiesStartingWith(""))
                .expectNext(0L)
                .verifyComplete();
    }

    @Test
    void getCitiesStartingWithShouldHandleNullOrEmptyInput() {
        StepVerifier.create(weatherService.getCitiesStartingWith(null))
                .expectNext(List.of())
                .verifyComplete();

        StepVerifier.create(weatherService.getCitiesStartingWith(""))
                .expectNext(List.of())
                .verifyComplete();
    }

    private City createCity(String name) {
        City city = new City();
        city.setName(name);
        return city;
    }
}
