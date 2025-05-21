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
