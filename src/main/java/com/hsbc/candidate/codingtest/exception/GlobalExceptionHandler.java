package com.hsbc.candidate.codingtest.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import jakarta.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

/**
 * Global exception handler for the application.
 * This class provides centralized exception handling across all controllers
 * and ensures consistent error responses for different types of exceptions.
 * It maps various exceptions to appropriate HTTP status codes and error messages.
 */
@ControllerAdvice
@Slf4j
@SuppressWarnings("PMD.TooManyMethods") // This class needs multiple methods to handle different exception types
public class GlobalExceptionHandler {

    /**
     * Default API path used when the request path cannot be determined.
     */
    private static final String DEFAULT_API_PATH = "/api";

    /**
     * Error code for internal server errors.
     */
    private static final String INTERNAL_SERVER_ERROR_CODE = "INTERNAL_SERVER_ERROR";

    /**
     * Error code for validation errors.
     */
    private static final String VALIDATION_ERROR_CODE = "VALIDATION_ERROR";

    /**
     * Error code for unhandled errors.
     */
    private static final String UNHANDLED_ERROR_CODE = "UNHANDLED_ERROR";

    /**
     * Error code for external service errors.
     */
    private static final String EXTERNAL_SERVICE_ERROR_CODE = "EXTERNAL_SERVICE_ERROR";

    /**
     * Error code for HTTP errors.
     */
    private static final String HTTP_ERROR_CODE = "HTTP_ERROR";



    /**
     * Handles ApplicationException by creating an appropriate error response.
     *
     * @param ex the ApplicationException to handle
     * @param exchange the current server exchange
     * @return a Mono emitting a ResponseEntity containing the error response
     */
    @ExceptionHandler(ApplicationException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleApplicationException(ApplicationException ex, ServerWebExchange exchange) {
        logError("Application exception", ex);
        return buildErrorResponse(ex.getHttpStatus(), ex.getErrorCode(), ex.getMessage(), null, exchange);
    }

    /**
     * Handles SystemException by creating an appropriate error response.
     * System exceptions are always mapped to INTERNAL_SERVER_ERROR status.
     *
     * @param ex the SystemException to handle
     * @param exchange the current server exchange
     * @return a Mono emitting a ResponseEntity containing the error response
     */
    @ExceptionHandler(SystemException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleSystemException(SystemException ex, ServerWebExchange exchange) {
        logError("System exception", ex);
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getErrorCode(), ex.getMessage(), null, exchange);
    }

    /**
     * Handles WebClientResponseException by creating an appropriate error response.
     * This exception occurs when there is an error in communication with external services.
     *
     * @param ex the WebClientResponseException to handle
     * @param exchange the current server exchange
     * @return a Mono emitting a ResponseEntity containing the error response
     */
    @ExceptionHandler(WebClientResponseException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleWebClientResponseException(WebClientResponseException ex, ServerWebExchange exchange) {
        logError("WebClient response exception", ex);
        return buildErrorResponse((HttpStatus) ex.getStatusCode(), EXTERNAL_SERVICE_ERROR_CODE, "Error from external service: " + ex.getMessage(), null, exchange);
    }

    /**
     * Handles ResponseStatusException by creating an appropriate error response.
     * This exception is typically thrown by Spring WebFlux when a request cannot be processed.
     *
     * @param ex the ResponseStatusException to handle
     * @param exchange the current server exchange
     * @return a Mono emitting a ResponseEntity containing the error response
     */
    @ExceptionHandler(ResponseStatusException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleResponseStatusException(ResponseStatusException ex, ServerWebExchange exchange) {
        logError("Response status exception", ex);
        return buildErrorResponse((HttpStatus) ex.getStatusCode(), HTTP_ERROR_CODE, ex.getReason(), null, exchange);
    }

    /**
     * Handles any Exception not caught by more specific exception handlers.
     * This is a fallback handler for unexpected exceptions.
     *
     * @param ex the Exception to handle
     * @param exchange the current server exchange
     * @return a Mono emitting a ResponseEntity containing the error response
     */
    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<ErrorResponse>> handleGlobalException(Exception ex, ServerWebExchange exchange) {
        logError("Unhandled exception", ex);
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, INTERNAL_SERVER_ERROR_CODE, "An unexpected error occurred", ex.getMessage(), exchange);
    }

    /**
     * Handles any Throwable not caught by more specific exception handlers.
     * This is the last resort handler for any unexpected errors that are not Exception instances.
     *
     * @param ex the Throwable to handle
     * @param exchange the current server exchange
     * @return a Mono emitting a ResponseEntity containing the error response
     */
    @ExceptionHandler(Throwable.class)
    public Mono<ResponseEntity<ErrorResponse>> handleThrowableException(Throwable ex, ServerWebExchange exchange) {
        logError("Unhandled throwable", ex);
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, UNHANDLED_ERROR_CODE, "An unexpected error occurred", null, exchange);
    }

    /**
     * Handles ConstraintViolationException by creating an appropriate error response.
     * This exception occurs when validation constraints on method parameters fail.
     * The response includes detailed information about each validation failure.
     *
     * @param ex the ConstraintViolationException to handle
     * @param exchange the current server exchange
     * @return a Mono emitting a ResponseEntity containing the error response
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleConstraintViolationException(ConstraintViolationException ex, ServerWebExchange exchange) {
        logError("Validation error", ex);
        String details = ex.getConstraintViolations().stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .collect(Collectors.joining(", "));
        return buildErrorResponse(HttpStatus.BAD_REQUEST, VALIDATION_ERROR_CODE, "Validation failed", details, exchange);
    }

    /**
     * Handles WebExchangeBindException by creating an appropriate error response.
     * This exception occurs when request body validation fails during data binding.
     * The response includes detailed information about each field validation failure.
     *
     * @param ex the WebExchangeBindException to handle
     * @param exchange the current server exchange
     * @return a Mono emitting a ResponseEntity containing the error response
     */
    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleWebExchangeBindException(WebExchangeBindException ex, ServerWebExchange exchange) {
        logError("Web exchange bind exception", ex);
        String details = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return buildErrorResponse(HttpStatus.BAD_REQUEST, VALIDATION_ERROR_CODE, "Validation failed", details, exchange);
    }

    /**
     * Logs error information with a consistent format.
     *
     * @param message a descriptive message about the error
     * @param ex the exception that was thrown
     */
    private void logError(String message, Throwable ex) {
        log.error("{}: {}", message, ex.getMessage(), ex);
    }

    /**
     * Builds a standardized error response with consistent structure.
     *
     * @param status the HTTP status code for the response
     * @param errorCode the application-specific error code
     * @param message the error message
     * @param details additional details about the error (can be null)
     * @param exchange the current server exchange
     * @return a Mono emitting a ResponseEntity containing the error response
     */
    private Mono<ResponseEntity<ErrorResponse>> buildErrorResponse(HttpStatus status, String errorCode, String message, String details, ServerWebExchange exchange) {
        String path = extractRequestPath(exchange);
        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode(errorCode)
                .message(message)
                .details(details)
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .path(path)
                .build();
        return Mono.just(new ResponseEntity<>(errorResponse, status));
    }

    /**
     * Extracts the request path from the server exchange.
     * If the exchange or request is null, returns a default API path.
     *
     * @param exchange the current server exchange
     * @return the request path or a default path if not available
     */
    private String extractRequestPath(ServerWebExchange exchange) {
        return exchange != null && null != exchange.getRequest()
                ? exchange.getRequest().getURI().getPath()
                : DEFAULT_API_PATH;
    }
}
