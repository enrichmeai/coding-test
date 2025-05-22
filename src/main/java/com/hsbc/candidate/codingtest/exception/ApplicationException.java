package com.hsbc.candidate.codingtest.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Base exception class for application-specific exceptions.
 * This class provides a consistent way to include error codes and HTTP status codes in exceptions.
 */
@Getter
public class ApplicationException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final String errorCode;
    private final HttpStatus httpStatus;

    /**
     * Constructs a new application exception with the specified error code, message, and HTTP status.
     *
     * @param errorCode the error code
     * @param message the detail message
     * @param httpStatus the HTTP status code
     */
    public ApplicationException(String errorCode, String message, HttpStatus httpStatus) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }

    /**
     * Constructs a new application exception with the specified error code, message, HTTP status, and cause.
     *
     * @param errorCode the error code
     * @param message the detail message
     * @param httpStatus the HTTP status code
     * @param cause the cause of the exception
     */
    public ApplicationException(String errorCode, String message, HttpStatus httpStatus, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }

    /**
     * Constructs a new application exception with the specified error code, message template, parameters, and HTTP status.
     *
     * @param errorCode the error code
     * @param messageTemplate the message template with placeholders for parameters
     * @param httpStatus the HTTP status code
     * @param params the parameters to be substituted in the message template
     */
    public ApplicationException(String errorCode, String messageTemplate, HttpStatus httpStatus, Object... params) {
        super(String.format(messageTemplate, params));
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }

    /**
     * Constructs a new application exception with the specified error code, message template, parameters, HTTP status, and cause.
     *
     * @param errorCode the error code
     * @param messageTemplate the message template with placeholders for parameters
     * @param httpStatus the HTTP status code
     * @param cause the cause of the exception
     * @param params the parameters to be substituted in the message template
     */
    public ApplicationException(String errorCode, String messageTemplate, HttpStatus httpStatus, Throwable cause, Object... params) {
        super(String.format(messageTemplate, params), cause);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }
}
