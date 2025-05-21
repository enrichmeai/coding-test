package com.hsbc.candidate.codingtest.exception;

import lombok.Getter;

/**
 * Base exception class for application-specific exceptions.
 * This class provides a consistent way to include error codes in exceptions.
 */
@Getter
public class ApplicationException extends RuntimeException {

    private final String errorCode;

    /**
     * Constructs a new application exception with the specified error code and message.
     *
     * @param errorCode the error code
     * @param message the detail message
     */
    public ApplicationException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    /**
     * Constructs a new application exception with the specified error code, message, and cause.
     *
     * @param errorCode the error code
     * @param message the detail message
     * @param cause the cause of the exception
     */
    public ApplicationException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
}
