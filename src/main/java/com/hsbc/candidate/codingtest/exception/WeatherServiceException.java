package com.hsbc.candidate.codingtest.exception;

/**
 * Exception thrown when there is an error related to the weather service.
 */
public class WeatherServiceException extends ApplicationException {

    /**
     * Error code for when the weather service is unavailable
     */
    public static final String SERVICE_UNAVAILABLE = "WEATHER_SERVICE_UNAVAILABLE";

    /**
     * Error code for when there is an error processing weather data
     */
    public static final String DATA_PROCESSING_ERROR = "WEATHER_DATA_PROCESSING_ERROR";

    /**
     * Error code for when the weather data is invalid
     */
    public static final String INVALID_DATA = "WEATHER_INVALID_DATA";

    /**
     * Constructs a new weather service exception with the specified error code and message.
     *
     * @param errorCode the error code
     * @param message the detail message
     */
    public WeatherServiceException(String errorCode, String message) {
        super(errorCode, message);
    }

    /**
     * Constructs a new weather service exception with the specified error code, message, and cause.
     *
     * @param errorCode the error code
     * @param message the detail message
     * @param cause the cause of the exception
     */
    public WeatherServiceException(String errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }
}
