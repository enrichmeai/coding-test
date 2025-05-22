package com.hsbc.candidate.codingtest.exception;

import lombok.Getter;

import java.io.Serial;

/**
 * Exception thrown when there is a system-level error.
 * This exception is used for errors related to the system infrastructure or configuration.
 */
@Getter
public class SystemException extends RuntimeException {

    /**
     * Serial version UID for serialization compatibility.
     */
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * The system-specific error code that identifies the type of system error.
     * This code can be used to categorize and handle different types of system errors.
     */
    private final String errorCode;


    /**
     * Constructs a new system exception with the specified error code and message.
     *
     * @param errorCode the error code
     * @param message the detail message
     */
    public SystemException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }


}
