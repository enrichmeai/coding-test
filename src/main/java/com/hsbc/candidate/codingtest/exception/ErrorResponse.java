package com.hsbc.candidate.codingtest.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * Standard error response object for the application.
 * This class provides a consistent structure for error responses.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {

    /**
     * Application-specific error code
     */
    private String errorCode;

    /**
     * Human-readable error message
     */
    private String message;

    /**
     * Timestamp when the error occurred
     */
    private LocalDateTime timestamp;

    /**
     * Additional details about the error (optional)
     */
    private String details;

    /**
     * HTTP status code
     */
    private int status;

    /**
     * Path of the request that caused the error
     */
    private String path;
}
