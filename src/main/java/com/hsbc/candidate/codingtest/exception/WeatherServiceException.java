package com.hsbc.candidate.codingtest.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when there is an error related to the weather service.
 * This class uses the ExceptionConstants enum for error codes, HTTP status codes, and message templates.
 */
public class WeatherServiceException extends ApplicationException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new weather service exception with the specified error type.
     *
     * @param exceptionType the exception type from ExceptionConstants
     * @param params the parameters to be substituted in the message template
     */
    public WeatherServiceException(ExceptionConstants exceptionType, Object... params) {
        super(exceptionType.getErrorCode(), exceptionType.getMessageTemplate(), exceptionType.getHttpStatus(), params);
    }

    /**
     * Constructs a new weather service exception with the specified error type and cause.
     *
     * @param exceptionType the exception type from ExceptionConstants
     * @param cause the cause of the exception
     * @param params the parameters to be substituted in the message template
     */
    public WeatherServiceException(ExceptionConstants exceptionType, Throwable cause, Object... params) {
        super(exceptionType.getErrorCode(), exceptionType.getMessageTemplate(), exceptionType.getHttpStatus(), cause, params);
    }

    /**
     * Constructs a new weather service exception with the specified error code, message, and HTTP status.
     *
     * @param errorCode the error code
     * @param message the detail message
     * @param httpStatus the HTTP status code
     */
    public WeatherServiceException(String errorCode, String message, HttpStatus httpStatus) {
        super(errorCode, message, httpStatus);
    }

    /**
     * Constructs a new weather service exception with the specified error code, message, HTTP status, and cause.
     *
     * @param errorCode the error code
     * @param message the detail message
     * @param httpStatus the HTTP status code
     * @param cause the cause of the exception
     */
    public WeatherServiceException(String errorCode, String message, HttpStatus httpStatus, Throwable cause) {
        super(errorCode, message, httpStatus, cause);
    }
}
