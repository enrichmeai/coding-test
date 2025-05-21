package com.hsbc.candidate.codingtest.exception;

import lombok.Getter;

/**
 * Exception thrown when there is a system-level error.
 * This exception is used for errors related to the system infrastructure or configuration.
 */
@Getter
public class SystemException extends RuntimeException {

    private final String errorCode;

    /**
     * Error code for when there is a configuration error
     */
    public static final String CONFIGURATION_ERROR = "SYSTEM_CONFIGURATION_ERROR";

    /**
     * Error code for when there is a resource unavailable error
     */
    public static final String RESOURCE_UNAVAILABLE = "SYSTEM_RESOURCE_UNAVAILABLE";

    /**
     * Error code for when there is a system timeout
     */
    public static final String SYSTEM_TIMEOUT = "SYSTEM_TIMEOUT";

    /**
     * Error code for when there is a general system error
     */
    public static final String GENERAL_ERROR = "SYSTEM_GENERAL_ERROR";

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

    /**
     * Constructs a new system exception with the specified error code, message, and cause.
     *
     * @param errorCode the error code
     * @param message the detail message
     * @param cause the cause of the exception
     */
    public SystemException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
}
