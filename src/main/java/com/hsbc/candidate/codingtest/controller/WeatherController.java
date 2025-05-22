package com.hsbc.candidate.codingtest.controller;

import com.hsbc.candidate.codingtest.model.WeatherResponse;
import com.hsbc.candidate.codingtest.service.WeatherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import jakarta.validation.constraints.Pattern;
import java.util.List;
import java.util.Map;

/**
 * REST controller for handling weather-related HTTP requests.
 * This controller provides endpoints for retrieving weather data and performing
 * operations on city data, such as filtering cities by their starting letter.
 */
@RestController
@RequestMapping("/api/weather")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
@Validated
public class WeatherController {

    /**
     * Service for handling weather-related operations.
     * This service is used to fetch weather data and perform operations on city data.
     */
    private final WeatherService weatherService;

    /**
     * Handles a GET request to retrieve all weather data. It invokes the weather service to fetch
     * weather-related information and wraps the response in an HTTP response entity.
     *
     * @return a {@code Mono} emitting the HTTP response entity containing the weather data encapsulated
     * in a {@code WeatherResponse}. In case of an error, it will propagate a {@code WeatherServiceException}.
     */
    @GetMapping
    public Mono<ResponseEntity<WeatherResponse>> getAllWeatherData() {
        log.info("Received request to get all weather data");
        return weatherService.fetchWeatherData()
                .map(ResponseEntity::ok);
    }

    /**
     * Counts the number of cities starting with the specified letter.
     *
     * @param letter the initial letter to filter cities by
     * @return a {@code Mono} emitting a {@code ResponseEntity} containing a {@code Map} with a single entry
     *         where the key is "count" and the value is the count of cities matching the criterion.
     *         In case of an error, it will propagate a {@code WeatherServiceException}.
     */
    @GetMapping("/cities/count")
    public Mono<ResponseEntity<Map<String, Long>>> countCitiesStartingWith(
            @RequestParam @Pattern(regexp = "^[a-zA-Z]$", message = "Letter must be a single alphabetic character") String letter) {
        log.info("Received request to count cities starting with: {}", letter);
        return weatherService.countCitiesStartingWith(letter)
                .map(count -> ResponseEntity.ok(Map.of("count", count)));
    }

    /**
     * Retrieves a list of city names that start with the specified letter.
     *
     * @param letter the initial letter to filter cities by
     * @return a {@code Mono} emitting a {@code ResponseEntity} containing a {@code List} of city names
     *         matching the initial letter criterion. In case of an error, it will propagate a
     *         {@code WeatherServiceException}.
     */
    @GetMapping("/cities")
    public Mono<ResponseEntity<List<String>>> getCitiesStartingWith(
            @RequestParam @Pattern(regexp = "^[a-zA-Z]$", message = "Letter must be a single alphabetic character") String letter) {
        log.info("Received request to get cities starting with: {}", letter);
        return weatherService.getCitiesStartingWith(letter)
                .map(ResponseEntity::ok);
    }
}
