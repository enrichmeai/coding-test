package com.hsbc.candidate.codingtest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

/**
 * Integration test for the Weather API.
 * This test starts the full application and uses a mock server to intercept requests to the external API.
 * It tests the application's REST endpoints by making HTTP requests to them and verifying the responses.
 */
@SpringBootTest(webEnvironment = RANDOM_PORT)
@ActiveProfiles("test")
class WeatherApiIntegrationTest {

    /**
     * Logger for this class.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(WeatherApiIntegrationTest.class);

    /**
     * The port that the application is running on.
     */
    @LocalServerPort
    private int port;

    /**
     * ObjectMapper for JSON serialization/deserialization.
     */
    @Autowired
    private ObjectMapper objectMapper;

    /**
     * WebTestClient for making HTTP requests to the application.
     */
    private WebTestClient webTestClient;

    /**
     * Mock HTTP server to simulate the external weather API.
     */
    private static HttpServer mockServer;

    /**
     * Port for the mock server, matching the port in application-test.yaml.
     */
    private static final int MOCK_SERVER_PORT = 8089; // Match the port in application-test.yaml

    /**
     * Sets up the mock server before all tests.
     * This server will intercept requests to the external API and return predefined responses.
     */
    @BeforeAll
    static void setupMockServer() throws IOException {
        mockServer = HttpServer.create(new InetSocketAddress(MOCK_SERVER_PORT), 0);

        // Create a context for the weather API endpoint
        mockServer.createContext("/data/2.5/box/city", new WeatherApiHandler());

        mockServer.setExecutor(null); // Use the default executor
        mockServer.start();
        // Use proper logging
        LOGGER.info("Mock server started on port {}", MOCK_SERVER_PORT);
    }

    /**
     * Tears down the mock server after all tests.
     */
    @AfterAll
    static void tearDownMockServer() {
        if (mockServer != null) {
            mockServer.stop(0);
            // Use proper logging
            LOGGER.info("Mock server stopped");
        }
    }

    /**
     * Sets up the WebTestClient before each test.
     */
    @org.junit.jupiter.api.BeforeEach
    void setup() {
        this.webTestClient = WebTestClient
                .bindToServer()
                .baseUrl("http://localhost:" + port)
                .build();
    }

    /**
     * Tests that the getAllWeatherData endpoint correctly returns weather data.
     */
    @Test
    void testGetAllWeatherData() {
        // Perform the test and store the response for assertion
        String responseBody = webTestClient.get()
                .uri("/api/weather")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .returnResult()
                .getResponseBody();

        // Add explicit assertions
        assertNotNull(responseBody, "Response body should not be null");
        assertTrue(responseBody.contains("\"cod\":\"200\""), "Response should contain success code");
        assertTrue(responseBody.contains("\"cnt\":2"), "Response should contain count of 2");
        assertTrue(responseBody.contains("\"name\":\"Zuwarah\""), "Response should contain Zuwarah");
        assertTrue(responseBody.contains("\"name\":\"Tripoli\""), "Response should contain Tripoli");
    }

    /**
     * Tests that the countCitiesStartingWith endpoint correctly returns the count of cities.
     */
    @Test
    void testCountCitiesStartingWith() {
        // Perform the test and store the response for assertion
        String responseBody = webTestClient.get()
                .uri("/api/weather/cities/count?letter=Z")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .returnResult()
                .getResponseBody();

        // Add explicit assertions
        assertNotNull(responseBody, "Response body should not be null");
        assertTrue(responseBody.contains("\"count\":1"), "Response should show count of 1 for cities starting with Z");
    }

    /**
     * Tests that the getCitiesStartingWith endpoint correctly returns a list of cities.
     */
    @Test
    void testGetCitiesStartingWith() {
        // Perform the test and store the response for assertion
        String responseBody = webTestClient.get()
                .uri("/api/weather/cities?letter=T")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .returnResult()
                .getResponseBody();

        // Add explicit assertions
        assertNotNull(responseBody, "Response body should not be null");
        assertTrue(responseBody.contains("Tripoli"), "Response should contain Tripoli");
        assertFalse(responseBody.contains("Zuwarah"), "Response should not contain Zuwarah");
    }

    /**
     * Handler for the mock weather API.
     * This handler returns a predefined response for the weather API endpoint.
     */
    static class WeatherApiHandler implements HttpHandler {
        /**
         * Handles HTTP requests to the mock weather API.
         *
         * @param exchange the HTTP exchange containing the request from the client and used to send the response
         * @throws IOException if an I/O error occurs
         */
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("GET".equals(exchange.getRequestMethod())) {
                // Create a sample weather response
                String response = createSampleWeatherResponse();

                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, response.getBytes().length);

                // Use try-with-resources to ensure the OutputStream is properly closed
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
            } else {
                exchange.sendResponseHeaders(405, -1); // Method not allowed
            }
        }

        /**
         * Creates a sample weather response in JSON format.
         * This response includes two cities: Zuwarah and Tripoli.
         *
         * @return a JSON string containing the sample weather data
         */
        private String createSampleWeatherResponse() {
            return """
                    {
                      "cod": "200",
                      "calctime": 0.0032,
                      "cnt": 2,
                      "list": [
                        {
                          "id": 2208425,
                          "name": "Zuwarah",
                          "coord": {
                            "lon": 12.08199,
                            "lat": 32.931198
                          },
                          "main": {
                            "temp": 293.25,
                            "temp_min": 293.15,
                            "temp_max": 293.45,
                            "pressure": 1023,
                            "sea_level": 1023,
                            "grnd_level": 1023,
                            "humidity": 100
                          },
                          "dt": 1560350192,
                          "wind": {
                            "speed": 3.1,
                            "deg": 209
                          },
                          "rain": null,
                          "clouds": {
                            "all": 0
                          },
                          "weather": [
                            {
                              "id": 800,
                              "main": "Clear",
                              "description": "clear sky",
                              "icon": "01d"
                            }
                          ]
                        },
                        {
                          "id": 2210247,
                          "name": "Tripoli",
                          "coord": {
                            "lon": 13.18746,
                            "lat": 32.875191
                          },
                          "main": {
                            "temp": 292.15,
                            "temp_min": 292.15,
                            "temp_max": 292.15,
                            "pressure": 1023,
                            "sea_level": 1023,
                            "grnd_level": 1023,
                            "humidity": 100
                          },
                          "dt": 1560350192,
                          "wind": {
                            "speed": 3.1,
                            "deg": 209
                          },
                          "rain": null,
                          "clouds": {
                            "all": 0
                          },
                          "weather": [
                            {
                              "id": 800,
                              "main": "Clear",
                              "description": "clear sky",
                              "icon": "01d"
                            }
                          ]
                        }
                      ]
                    }""";
        }
    }
}
