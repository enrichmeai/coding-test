package com.hsbc.candidate.codingtest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

/**
 * Integration test for the Weather API.
 * This test starts the full application and uses a mock server to intercept requests to the external API.
 * It tests the application's REST endpoints by making HTTP requests to them and verifying the responses.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class WeatherApiIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private ObjectMapper objectMapper;

    private WebTestClient webTestClient;

    private static HttpServer mockServer;
    private static final int MOCK_SERVER_PORT = 8089; // Match the port in application-test.yaml

    /**
     * Sets up the mock server before all tests.
     * This server will intercept requests to the external API and return predefined responses.
     */
    @BeforeAll
    public static void setupMockServer() throws IOException {
        mockServer = HttpServer.create(new InetSocketAddress(MOCK_SERVER_PORT), 0);

        // Create a context for the weather API endpoint
        mockServer.createContext("/data/2.5/box/city", new WeatherApiHandler());

        mockServer.setExecutor(null); // Use the default executor
        mockServer.start();
        System.out.println("Mock server started on port " + MOCK_SERVER_PORT);
    }

    /**
     * Tears down the mock server after all tests.
     */
    @AfterAll
    public static void tearDownMockServer() {
        if (mockServer != null) {
            mockServer.stop(0);
            System.out.println("Mock server stopped");
        }
    }

    /**
     * Sets up the WebTestClient before each test.
     */
    @org.junit.jupiter.api.BeforeEach
    public void setup() {
        this.webTestClient = WebTestClient
                .bindToServer()
                .baseUrl("http://localhost:" + port)
                .build();
    }

    /**
     * Tests that the getAllWeatherData endpoint correctly returns weather data.
     */
    @Test
    public void testGetAllWeatherData() {
        webTestClient.get()
                .uri("/api/weather")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.cod").isEqualTo("200")
                .jsonPath("$.cnt").isEqualTo(2)
                .jsonPath("$.list[0].name").isEqualTo("Zuwarah")
                .jsonPath("$.list[1].name").isEqualTo("Tripoli");
    }

    /**
     * Tests that the countCitiesStartingWith endpoint correctly returns the count of cities.
     */
    @Test
    public void testCountCitiesStartingWith() {
        webTestClient.get()
                .uri("/api/weather/cities/count?letter=Z")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.count").isEqualTo(1);
    }

    /**
     * Tests that the getCitiesStartingWith endpoint correctly returns a list of cities.
     */
    @Test
    public void testGetCitiesStartingWith() {
        webTestClient.get()
                .uri("/api/weather/cities?letter=T")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0]").isEqualTo("Tripoli");
    }

    /**
     * Handler for the mock weather API.
     * This handler returns a predefined response for the weather API endpoint.
     */
    static class WeatherApiHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("GET".equals(exchange.getRequestMethod())) {
                // Create a sample weather response
                String response = createSampleWeatherResponse();

                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, response.getBytes().length);

                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            } else {
                exchange.sendResponseHeaders(405, -1); // Method not allowed
            }
        }

        /**
         * Creates a sample weather response in JSON format.
         * This response includes two cities: Zuwarah and Tripoli.
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
