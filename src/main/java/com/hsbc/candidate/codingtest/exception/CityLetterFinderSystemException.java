package com.hsbc.candidate.codingtest.exception;

/**
 * Exception thrown when there is a system-level error in the City Letter Finder component.
 * This exception extends SystemException to provide specific error handling for the City Letter Finder functionality.
 */
public class CityLetterFinderSystemException extends SystemException {

    /**
     * Error code for when there is a city letter finder error
     */
    public static final String CITY_LETTER_FINDER_ERROR = "CITY_LETTER_FINDER_ERROR";

    /**
     * Constructs a new city letter finder system exception with the specified error code and message.
     *
     * @param errorCode the error code
     * @param message the detail message
     */
    public CityLetterFinderSystemException(String errorCode, String message) {
        super(errorCode, message);
    }

    /**
     * Constructs a new city letter finder system exception with the specified error code, message, and cause.
     *
     * @param errorCode the error code
     * @param message the detail message
     * @param cause the cause of the exception
     */
    public CityLetterFinderSystemException(String errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }
}
