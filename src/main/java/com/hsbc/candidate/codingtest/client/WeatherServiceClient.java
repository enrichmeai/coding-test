package com.hsbc.candidate.codingtest.client;

import com.hsbc.candidate.codingtest.model.WeatherResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;

/**
 * A client for interacting with an external weather service to fetch weather data.
 * This class utilizes a reactive approach with WebClient for making HTTP requests.
 */
@Component
@Slf4j
public class WeatherServiceClient {

    /**
     * The WebClient instance used to make HTTP requests to the weather API.
     */
    private final WebClient webClient;

    /**
     * The URL of the weather API endpoint.
     */
    private final String apiUrl;

    /**
     * The number of retry attempts for failed API calls.
     */
    private static final int RETRY_ATTEMPTS = 3;

    /**
     * The delay between retry attempts.
     */
    private static final Duration RETRY_DELAY = Duration.ofSeconds(2);

    /**
     * Constructs a new WeatherServiceClient with the specified WebClient and API URL.
     *
     * @param weatherServiceWebClient the WebClient to use for API requests
     * @param weatherApiUrl the URL of the weather API endpoint
     */
    public WeatherServiceClient(@Qualifier("openWeatherApiWebClient") WebClient weatherServiceWebClient,
                                @Qualifier("openWeatherApiUrl") String weatherApiUrl) {
        this.webClient = weatherServiceWebClient;
        this.apiUrl = weatherApiUrl;
    }

    /**
     * Fetches weather data from the external weather service API.
     * This method makes a GET request to the configured API URL and deserializes
     * the response into a WeatherResponse object. It includes retry logic for
     * handling transient failures.
     *
     * @return a Mono emitting the weather data response
     */
    public Mono<WeatherResponse> fetchWeatherData() {
        return webClient.get()
                .uri(apiUrl)
                .retrieve()
                .bodyToMono(WeatherResponse.class)
                .retryWhen(createRetrySpec());
    }

    /**
     * Creates a retry specification for handling transient failures in API calls.
     * This method configures a fixed delay retry strategy that will retry failed
     * requests a specified number of times with a fixed delay between attempts.
     * Only WebClientResponseException errors will trigger retries.
     *
     * @return a configured Retry specification
     */
    private Retry createRetrySpec() {
        return Retry.fixedDelay(RETRY_ATTEMPTS, RETRY_DELAY)
                .filter(throwable -> throwable instanceof WebClientResponseException)
                .onRetryExhaustedThrow((retryBackoffSpec, signal) -> signal.failure());
    }

}
