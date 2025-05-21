package com.hsbc.candidate.codingtest.client;

import com.hsbc.candidate.codingtest.exception.CityLetterFinderSystemException;
import com.hsbc.candidate.codingtest.exception.WeatherServiceException;
import com.hsbc.candidate.codingtest.model.WeatherResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;

@Component
@Slf4j
public class WeatherServiceClient {

    private final WebClient webClient;
    private final String apiUrl;

    public WeatherServiceClient(@Qualifier("openWeatherApiWebClient") WebClient weatherServiceWebClient,
                                @Qualifier("openWeatherApiUrl") String weatherApiUrl) {
        this.webClient = weatherServiceWebClient;
        this.apiUrl = weatherApiUrl;
    }

    private static final int RETRY_ATTEMPTS = 3;
    private static final Duration RETRY_DELAY = Duration.ofSeconds(2);

    public Mono<WeatherResponse> fetchWeatherData() {
        return webClient.get()
                .uri(apiUrl)
                .retrieve()
                .bodyToMono(WeatherResponse.class)
                .retryWhen(createRetrySpec())
                .doOnError(e -> log.error("Error fetching weather data: {}", e.getMessage()))
                .onErrorMap(WebClientResponseException.class, this::mapToWeatherServiceException)
                .onErrorMap(e -> !(e instanceof WeatherServiceException), this::mapToSystemException);
    }

    private Retry createRetrySpec() {
        return Retry.fixedDelay(RETRY_ATTEMPTS, RETRY_DELAY)
                .filter(throwable -> throwable instanceof WebClientResponseException)
                .onRetryExhaustedThrow((retryBackoffSpec, signal) ->
                        new WeatherServiceException(
                                WeatherServiceException.SERVICE_UNAVAILABLE,
                                "Retry attempts exhausted while fetching weather data",
                                signal.failure()
                        ));
    }

    private WeatherServiceException mapToWeatherServiceException(WebClientResponseException e) {
        return new WeatherServiceException(
                WeatherServiceException.SERVICE_UNAVAILABLE,
                "Failed to fetch weather data from external service",
                e
        );
    }

    private CityLetterFinderSystemException mapToSystemException(Throwable e) {
        return new CityLetterFinderSystemException(
                WeatherServiceException.DATA_PROCESSING_ERROR,
                "Error processing weather data: " + e.getMessage(),
                e
        );
    }
}
