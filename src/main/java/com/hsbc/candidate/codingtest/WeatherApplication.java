package com.hsbc.candidate.codingtest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WeatherApplication {

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private WeatherApplication() {
        // Private constructor to hide the implicit public one
    }

    public static void main(String[] args) {
        SpringApplication.run(WeatherApplication.class, args);
    }

}
