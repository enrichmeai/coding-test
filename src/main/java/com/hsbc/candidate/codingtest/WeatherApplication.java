package com.hsbc.candidate.codingtest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for the Weather Service application.
 * This class is responsible for bootstrapping and launching the Spring Boot application.
 */
@SpringBootApplication
@SuppressWarnings("checkstyle:FinalClass")
public class WeatherApplication {

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private WeatherApplication() {
        // Private constructor to hide the implicit public one
    }

    /**
     * Main method that starts the Spring Boot application.
     *
     * @param args command line arguments passed to the application
     */
    public static void main(String[] args) {
        SpringApplication.run(WeatherApplication.class, args);
    }

}
