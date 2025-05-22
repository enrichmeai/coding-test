package com.hsbc.candidate.codingtest.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Enum-based constants for exception handling.
 * This enum centralizes all error codes, HTTP status codes, and message templates used in the application.
 * Using an enum makes it easier to manage and ensures consistency between error codes, statuses, and messages.
 */
@Getter
public enum ExceptionConstants {

    // Validation errors
    VALIDATION_ERROR("VALIDATION_ERROR", HttpStatus.BAD_REQUEST, "Validation error: %s"),

    // Resource errors
    RESOURCE_NOT_FOUND("RESOURCE_NOT_FOUND", HttpStatus.NOT_FOUND, "Resource not found: %s"),
    RESOURCE_UNAVAILABLE("RESOURCE_UNAVAILABLE", HttpStatus.SERVICE_UNAVAILABLE, "Resource unavailable: %s"),

    // External service errors
    EXTERNAL_SERVICE_ERROR("EXTERNAL_SERVICE_ERROR", HttpStatus.SERVICE_UNAVAILABLE, "Error from external service: %s"),

    // Weather service specific errors
    WEATHER_SERVICE_UNAVAILABLE("WEATHER_SERVICE_UNAVAILABLE", HttpStatus.SERVICE_UNAVAILABLE, "Weather service is unavailable: %s"),
    WEATHER_DATA_PROCESSING_ERROR("WEATHER_DATA_PROCESSING_ERROR", HttpStatus.INTERNAL_SERVER_ERROR, "Error processing weather data: %s"),
    WEATHER_DATA_INVALID("WEATHER_DATA_INVALID", HttpStatus.BAD_REQUEST, "Invalid weather data: %s"),

    // System errors
    INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR", HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred: %s"),
    CITY_LETTER_FINDER_ERROR("CITY_LETTER_FINDER_ERROR", HttpStatus.INTERNAL_SERVER_ERROR, "City letter finder error: %s"),
    CONFIGURATION_ERROR("CONFIGURATION_ERROR", HttpStatus.INTERNAL_SERVER_ERROR, "Configuration error: %s"),
    SYSTEM_TIMEOUT("SYSTEM_TIMEOUT", HttpStatus.GATEWAY_TIMEOUT, "System timeout: %s"),
    GENERAL_SYSTEM_ERROR("GENERAL_SYSTEM_ERROR", HttpStatus.INTERNAL_SERVER_ERROR, "System error: %s");

    /**
     * The error code that uniquely identifies this exception type.
     * This code is used in error responses and can be used by clients to identify the error.
     */
    private final String errorCode;

    /**
     * The HTTP status code that should be returned when this exception occurs.
     * This determines the HTTP response status in the API response.
     */
    private final HttpStatus httpStatus;

    /**
     * The message template with placeholders for dynamic parameters.
     * This template is used to generate the error message with specific details.
     */
    private final String messageTemplate;

    /**
     * Constructor for ExceptionConstants enum.
     *
     * @param errorCode the error code
     * @param httpStatus the HTTP status code
     * @param messageTemplate the message template with placeholders for parameters
     */
    ExceptionConstants(String errorCode, HttpStatus httpStatus, String messageTemplate) {
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
        this.messageTemplate = messageTemplate;
    }

    /**
     * Creates an ApplicationException with this error type.
     *
     * @param params the parameters to be substituted in the message template
     * @return a new ApplicationException
     */
    public ApplicationException createException(Object... params) {
        return new ApplicationException(errorCode, messageTemplate, httpStatus, params);
    }

    /**
     * Creates an ApplicationException with this error type and a cause.
     *
     * @param cause the cause of the exception
     * @param params the parameters to be substituted in the message template
     * @return a new ApplicationException
     */
    public ApplicationException createException(Throwable cause, Object... params) {
        return new ApplicationException(errorCode, messageTemplate, httpStatus, cause, params);
    }
}
