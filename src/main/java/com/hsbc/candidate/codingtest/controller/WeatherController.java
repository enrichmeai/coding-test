package com.hsbc.candidate.codingtest.controller;

import com.hsbc.candidate.codingtest.exception.WeatherServiceException;
import com.hsbc.candidate.codingtest.model.WeatherResponse;
import com.hsbc.candidate.codingtest.service.WeatherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/weather")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
public class WeatherController {

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
                .map(ResponseEntity::ok)
                .doOnError(e -> log.error("Error in getAllWeatherData endpoint", e))
                .onErrorResume(e -> {
                    if (!(e instanceof WeatherServiceException)) {
                        return Mono.error(new WeatherServiceException(
                                WeatherServiceException.DATA_PROCESSING_ERROR,
                                "Error processing weather data in controller layer",
                                e
                        ));
                    }
                    return Mono.error(e);
                });
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
    public Mono<ResponseEntity<Map<String, Long>>> countCitiesStartingWith(@RequestParam String letter) {
        log.info("Received request to count cities starting with: {}", letter);
        return weatherService.countCitiesStartingWith(letter)
                .map(count -> ResponseEntity.ok(Map.of("count", count)))
                .doOnError(e -> log.error("Error in countCitiesStartingWith endpoint with letter: {}", letter, e))
                .onErrorResume(e -> {
                    if (!(e instanceof WeatherServiceException)) {
                        return Mono.error(new WeatherServiceException(
                                WeatherServiceException.DATA_PROCESSING_ERROR,
                                "Error counting cities in controller layer",
                                e
                        ));
                    }
                    return Mono.error(e);
                });
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
    public Mono<ResponseEntity<List<String>>> getCitiesStartingWith(@RequestParam String letter) {
        log.info("Received request to get cities starting with: {}", letter);
        return weatherService.getCitiesStartingWith(letter)
                .map(ResponseEntity::ok)
                .doOnError(e -> log.error("Error in getCitiesStartingWith endpoint with letter: {}", letter, e))
                .onErrorResume(e -> {
                    if (!(e instanceof WeatherServiceException)) {
                        return Mono.error(new WeatherServiceException(
                                WeatherServiceException.DATA_PROCESSING_ERROR,
                                "Error retrieving cities in controller layer",
                                e
                        ));
                    }
                    return Mono.error(e);
                });
    }
}
