package com.hsbc.candidate.codingtest.service;

import com.hsbc.candidate.codingtest.client.WeatherServiceClient;
import com.hsbc.candidate.codingtest.exception.WeatherServiceException;
import com.hsbc.candidate.codingtest.model.City;
import com.hsbc.candidate.codingtest.model.WeatherResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class WeatherService {

    private final WeatherServiceClient weatherServiceClient;

    public Mono<WeatherResponse> fetchWeatherData() {
        return weatherServiceClient.fetchWeatherData()
                .onErrorMap(e -> !(e instanceof WeatherServiceException), e -> {
                    log.error("Unexpected error in fetchWeatherData", e);
                    return new WeatherServiceException(
                            WeatherServiceException.DATA_PROCESSING_ERROR,
                            "Error processing weather data in service layer",
                            e
                    );
                });
    }

    public Mono<Long> countCitiesStartingWith(String letter) {
        if (letter == null || letter.isEmpty()) {
            return Mono.just(0L);
        }

        String letterLowerCase = letter.toLowerCase();

        return fetchWeatherData()
                .handle((response, sink) -> {
                    if (response.getCities() == null) {
                        sink.error(new WeatherServiceException(
                                WeatherServiceException.INVALID_DATA,
                                "Weather response contains no cities data"
                        ));
                        return;
                    }
                    sink.next(response.getCities().stream()
                            .filter(city -> city.getName() != null &&
                                    city.getName().toLowerCase().startsWith(letterLowerCase))
                            .count());
                });
    }

    public Mono<List<String>> getCitiesStartingWith(String letter) {
        if (letter == null || letter.isEmpty()) {
            return Mono.just(List.of());
        }

        String letterLowerCase = letter.toLowerCase();

        return fetchWeatherData()
                .handle((response, sink) -> {
                    if (response.getCities() == null) {
                        sink.error(new WeatherServiceException(
                                WeatherServiceException.INVALID_DATA,
                                "Weather response contains no cities data"
                        ));
                        return;
                    }
                    sink.next(response.getCities().stream()
                            .filter(city -> city.getName() != null &&
                                    city.getName().toLowerCase().startsWith(letterLowerCase))
                            .map(City::getName)
                            .collect(Collectors.toList()));
                });
    }
}
