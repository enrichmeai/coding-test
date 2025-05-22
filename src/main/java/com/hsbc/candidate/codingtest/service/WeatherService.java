package com.hsbc.candidate.codingtest.service;

import com.hsbc.candidate.codingtest.client.WeatherServiceClient;
import com.hsbc.candidate.codingtest.exception.ExceptionConstants;
import com.hsbc.candidate.codingtest.exception.WeatherServiceException;
import com.hsbc.candidate.codingtest.model.City;
import com.hsbc.candidate.codingtest.model.WeatherResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Service class for handling weather-related operations.
 * This class provides methods to fetch weather data and perform operations on city data,
 * such as filtering cities by their starting letter.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class WeatherService {

    /**
     * Client for interacting with the external weather service API.
     * This client is used to fetch weather data from the external service.
     */
    private final WeatherServiceClient weatherServiceClient;

    /**
     * Fetches weather data by communicating with the weather service client.
     *
     * @return a {@code Mono} containing the weather data in a {@code WeatherResponse} object,
     * or an error if the operation fails.
     */
    public Mono<WeatherResponse> fetchWeatherData() {
        return weatherServiceClient.fetchWeatherData();
    }

    /**
     * Counts the number of cities whose names start with the given letter.
     *
     * @param startingLetter the starting letter to filter city names; if null or empty, the method returns 0.
     * @return a {@code Mono} emitting the count of cities whose names start with the specified letter,
     * or 0 if no such cities exist.
     */
    public Mono<Long> countCitiesStartingWith(String startingLetter) {
        if (isNullOrEmpty(startingLetter)) {
            return Mono.just(0L);
        }

        String lowerCaseLetter = startingLetter.toLowerCase(Locale.ROOT);

        return fetchWeatherData()
                .map(this::validateWeatherResponse) // Validate and extract cities
                .map(cities -> cities.stream()
                        .filter(city -> cityNameStartsWith(city, lowerCaseLetter))
                        .count())
                .onErrorMap(this::handleError); // Handle unexpected errors
    }

    /**
     * Retrieves a list of city names starting with the specified letter.
     * If no cities match or the input letter is null/empty, an empty list is returned.
     *
     * @param startingLetter the starting letter to filter city names; should not be null or empty.
     * @return a {@code Mono} emitting a list of city names that start with the specified letter,
     * or an empty list if no such cities exist or data is unavailable.
     */
    public Mono<List<String>> getCitiesStartingWith(String startingLetter) {
        if (isNullOrEmpty(startingLetter)) {
            return Mono.just(List.of());
        }

        String lowerCaseLetter = startingLetter.toLowerCase(Locale.ROOT);

        return fetchWeatherData()
                .map(this::validateWeatherResponse) // Validate and extract cities
                .map(cities -> cities.stream()
                        .filter(city -> cityNameStartsWith(city, lowerCaseLetter))
                        .map(City::getName)
                        .collect(Collectors.toList()))
                .onErrorMap(this::handleError); // Handle unexpected errors
    }

    /**
     * Validates the weather response and ensures city data is available.
     *
     * @param response the weather response to validate
     * @return the list of cities if the response is valid
     * @throws WeatherServiceException if the cities data is null
     */
    private List<City> validateWeatherResponse(WeatherResponse response) {
        if (response.getCities() == null) {
            throw new WeatherServiceException(
                    ExceptionConstants.WEATHER_DATA_INVALID,
                    "Weather response contains no cities data"
            );
        }
        return response.getCities();
    }

    /**
     * Checks if the city name starts with the given letter.
     *
     * @param city            the city object
     * @param lowerCaseLetter the filter letter in lowercase
     * @return {@code true} if the city's name starts with the letter, {@code false} otherwise
     */
    private boolean cityNameStartsWith(City city, String lowerCaseLetter) {
        return city.getName() != null && city.getName().toLowerCase(Locale.ROOT).startsWith(lowerCaseLetter);
    }

    /**
     * Checks if the provided value is null or empty.
     *
     * @param value the string to check
     * @return {@code true} if the string is null or empty, {@code false} otherwise
     */
    private boolean isNullOrEmpty(String value) {
        return value == null || value.isEmpty();
    }

    /**
     * Handles unexpected errors by wrapping them into a WeatherServiceException if needed.
     *
     * @param throwable the original exception
     * @return a WeatherServiceException representing the error
     */
    private WeatherServiceException handleError(Throwable throwable) {
        if (throwable instanceof WeatherServiceException) {
            return (WeatherServiceException) throwable;
        }

        log.error("Unexpected error in WeatherService", throwable);
        return new WeatherServiceException(
                ExceptionConstants.WEATHER_DATA_PROCESSING_ERROR,
                throwable,
                "An unexpected error occurred in the service layer"
        );
    }
}
