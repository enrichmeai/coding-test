package com.hsbc.candidate.codingtest.service;

import com.hsbc.candidate.codingtest.client.WeatherServiceClient;
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

/**
 * Unit tests for the WeatherService class.
 * These tests verify that the service correctly interacts with the WeatherServiceClient
 * to fetch weather data and perform operations on city data, such as filtering cities
 * by their starting letter.
 */
@ExtendWith(MockitoExtension.class)
@SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert") // Tests use StepVerifier for reactive stream assertions
class WeatherServiceTest {

    /**
     * Mock of the WeatherServiceClient used by the service.
     * This is mocked to control the client's behavior in tests.
     */
    @Mock
    private WeatherServiceClient weatherServiceClient;

    /**
     * The WeatherService instance being tested.
     * This is injected with the mock WeatherServiceClient.
     */
    @InjectMocks
    private WeatherService weatherService;

    @Test
    void fetchWeatherDataShouldReturnWeatherResponseWhenSuccessful() {
        WeatherResponse mockResponse = new WeatherResponse();

        when(weatherServiceClient.fetchWeatherData()).thenReturn(Mono.just(mockResponse));

        StepVerifier.create(weatherService.fetchWeatherData())
                .expectNext(mockResponse)
                .verifyComplete();
    }

    @Test
    void fetchWeatherDataShouldPropagateExceptions() {
        RuntimeException originalException = new RuntimeException("Unexpected Error");
        when(weatherServiceClient.fetchWeatherData()).thenReturn(Mono.error(originalException));

        StepVerifier.create(weatherService.fetchWeatherData())
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    void fetchWeatherDataShouldHandleNullResponse() {
        when(weatherServiceClient.fetchWeatherData()).thenReturn(Mono.empty());

        StepVerifier.create(weatherService.fetchWeatherData())
                .expectComplete()
                .verify();
    }

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
    void testGetCitiesStartingWithReturnsCorrectCities() {
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
    void testGetCitiesStartingWithHandlesNullOrEmptyInput() {
        StepVerifier.create(weatherService.getCitiesStartingWith(null))
                .expectNext(List.of())
                .verifyComplete();

        StepVerifier.create(weatherService.getCitiesStartingWith(""))
                .expectNext(List.of())
                .verifyComplete();
    }

    /**
     * Creates a City object with the specified name.
     *
     * @param name the name to set for the city
     * @return a new City object with the specified name
     */
    private City createCity(String name) {
        City city = new City();
        city.setName(name);
        return city;
    }
}
