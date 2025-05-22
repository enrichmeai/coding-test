package com.hsbc.candidate.codingtest.controller;

import com.hsbc.candidate.codingtest.exception.GlobalExceptionHandler;
import com.hsbc.candidate.codingtest.model.City;
import com.hsbc.candidate.codingtest.model.WeatherResponse;
import com.hsbc.candidate.codingtest.service.WeatherService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;

import static org.mockito.Mockito.when;

/**
 * Unit tests for the WeatherController class.
 * These tests verify that the controller correctly handles HTTP requests,
 * interacts with the WeatherService, and returns appropriate responses.
 * The tests use WebTestClient for testing the HTTP endpoints and StepVerifier
 * for testing the reactive streams.
 */
@SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
class WeatherControllerTest {

    /**
     * Constant for the city name "New York" used in multiple test methods.
     */
    private static final String NEW_YORK = "New York";

    /**
     * Constant for the city name "Nashville" used in multiple test methods.
     */
    private static final String NASHVILLE = "Nashville";

    /**
     * Constant for the letter "N" used as a filter parameter in multiple test methods.
     */
    private static final String LETTER_N = "N";

    /**
     * Constant for the invalid filter "ABC" used in tests for invalid input handling.
     */
    private static final String LETTER_ABC = "ABC";

    /**
     * Constant for the numeric filter "1" used in tests for invalid input handling.
     */
    private static final String LETTER_1 = "1";

    /**
     * WebTestClient instance used to test HTTP endpoints.
     * This client allows testing the controller without starting a full HTTP server.
     */
    private WebTestClient webTestClient;

    /**
     * Mock of the WeatherService used by the controller.
     * This is mocked to control the service's behavior in tests.
     */
    @Mock
    private WeatherService weatherService;

    /**
     * Sets up the test environment before each test.
     * Initialize mocks, creates the controller, and configures the WebTestClient.
     * Also sets up common mock behaviors for the WeatherService.
     */
    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        WeatherController controller = new WeatherController(weatherService);

        webTestClient = WebTestClient
                .bindToController(controller)
                .controllerAdvice(new GlobalExceptionHandler())
                .build();

        when(weatherService.countCitiesStartingWith(LETTER_N)).thenReturn(Mono.just(1L));
        when(weatherService.getCitiesStartingWith(LETTER_N)).thenReturn(Mono.just(Arrays.asList(NEW_YORK, NASHVILLE)));

        when(weatherService.countCitiesStartingWith(LETTER_ABC)).thenReturn(Mono.empty());
        when(weatherService.countCitiesStartingWith(LETTER_1)).thenReturn(Mono.empty());
        when(weatherService.getCitiesStartingWith(LETTER_ABC)).thenReturn(Mono.empty());
        when(weatherService.getCitiesStartingWith(LETTER_1)).thenReturn(Mono.empty());
    }

    /**
     * Tests that the getAllWeatherData endpoint correctly returns weather data.
     * This test verifies that:
     * 1. The service method is called and returns the expected data
     * 2. The controller returns the correct HTTP status and response body
     * The test uses both StepVerifier for testing the reactive service and
     * WebTestClient for testing the HTTP endpoint.
     */
    @Test
    void testGetAllWeatherData() {
        // Create test data
        WeatherResponse response = new WeatherResponse();
        response.setCod("200");
        response.setCalctime(0.1234);
        response.setCnt(2);
        response.setName("Test Weather Data");

        City city1 = new City();
        city1.setId(1L);
        city1.setName(NEW_YORK);

        City city2 = new City();
        city2.setId(2L);
        city2.setName("London");

        response.setCities(Arrays.asList(city1, city2));

        // Mock service behavior
        when(weatherService.fetchWeatherData()).thenReturn(Mono.just(response));

        // Verify service behavior using StepVerifier
        StepVerifier.create(weatherService.fetchWeatherData())
                .expectNext(response)
                .verifyComplete();

        // Test HTTP endpoint using WebTestClient
        webTestClient.get()
                .uri("/api/weather")
                .exchange()
                .expectStatus().isOk()
                .expectBody(WeatherResponse.class)
                .isEqualTo(response);
    }


    /**
     * Tests that the countCitiesStartingWith endpoint correctly returns the count of cities.
     * This test verifies that:
     * 1. The service method is called and returns the expected count
     * 2. The controller returns the correct HTTP status and response body
     * The test uses both StepVerifier for testing the reactive service and
     * WebTestClient for testing the HTTP endpoint.
     */
    @Test
    void testCountCitiesStartingWith() {
        // Mock service behavior
        when(weatherService.countCitiesStartingWith(LETTER_N)).thenReturn(Mono.just(1L));

        // Verify service behavior using StepVerifier
        StepVerifier.create(weatherService.countCitiesStartingWith(LETTER_N))
                .expectNext(1L)
                .verifyComplete();

        // Test HTTP endpoint using WebTestClient
        webTestClient.get()
                .uri("/api/weather/cities/count?letter=" + LETTER_N)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.count").isEqualTo(1);
    }

    /**
     * Tests that the getCitiesStartingWith endpoint correctly returns a list of cities.
     * This test verifies that the controller returns the correct HTTP status and response body
     * containing the list of cities starting with the specified letter.
     */
    @Test
    void testGetCitiesStartingWith() {
        // Mock service behavior
        when(weatherService.getCitiesStartingWith(LETTER_N)).thenReturn(Mono.just(Arrays.asList(NEW_YORK, NASHVILLE)));

        // Test HTTP endpoint using WebTestClient
        webTestClient.get()
                .uri("/api/weather/cities?letter=" + LETTER_N)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0]").isEqualTo(NEW_YORK)
                .jsonPath("$[1]").isEqualTo(NASHVILLE);
    }

    /**
     * Tests that the countCitiesStartingWith endpoint handles invalid input gracefully.
     * This test verifies that when a letter parameter that is too long is provided,
     * the controller still returns a successful response.
     */
    @Test
    void testCountCitiesStartingWithInvalidLetterTooLong() {
        // Test HTTP endpoint using WebTestClient
        webTestClient.get()
                .uri("/api/weather/cities/count?letter=" + LETTER_ABC)
                .exchange()
                .expectStatus().isOk();
    }

    /**
     * Tests that the countCitiesStartingWith endpoint handles invalid input gracefully.
     * This test verifies that when a numeric parameter is provided instead of a letter,
     * the controller still returns a successful response.
     */
    @Test
    void testCountCitiesStartingWithInvalidLetterNumber() {
        // Test HTTP endpoint using WebTestClient
        webTestClient.get()
                .uri("/api/weather/cities/count?letter=" + LETTER_1)
                .exchange()
                .expectStatus().isOk();
    }

    /**
     * Tests that the getCitiesStartingWith endpoint handles invalid input gracefully.
     * This test verifies that when a letter parameter that is too long is provided,
     * the controller still returns a successful response.
     */
    @Test
    void testGetCitiesStartingWithInvalidLetterTooLong() {
        // Test HTTP endpoint using WebTestClient
        webTestClient.get()
                .uri("/api/weather/cities?letter=" + LETTER_ABC)
                .exchange()
                .expectStatus().isOk();
    }

    /**
     * Tests that the getCitiesStartingWith endpoint handles invalid input gracefully.
     * This test verifies that when a numeric parameter is provided instead of a letter,
     * the controller still returns a successful response.
     */
    @Test
    void testGetCitiesStartingWithInvalidLetterNumber() {
        // Test HTTP endpoint using WebTestClient
        webTestClient.get()
                .uri("/api/weather/cities?letter=" + LETTER_1)
                .exchange()
                .expectStatus().isOk();
    }
}
