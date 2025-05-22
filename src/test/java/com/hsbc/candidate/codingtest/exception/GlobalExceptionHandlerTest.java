package com.hsbc.candidate.codingtest.exception;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.test.StepVerifier;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import java.net.URI;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Integration tests for the GlobalExceptionHandler class.
 * These tests verify that the exception handler correctly processes different types of exceptions
 * and returns appropriate error responses with the correct status codes and error messages.
 * The tests use StepVerifier for reactive stream assertions.
 */
@SpringBootTest
@SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert") // Tests use StepVerifier for assertions
class GlobalExceptionHandlerTest {

    // Constants to avoid duplicate string literals in assertion messages
    private static final String MSG_ERROR_CODE = "Error code should match the one from the exception";
    private static final String MSG_ERROR_MESSAGE = "Error message should match the one from the exception";
    private static final String MSG_HTTP_STATUS = "HTTP status code should match the one from the exception";
    private static final String MSG_PATH = "Path should match the request URI path";
    private static final String MSG_STATUS_CODE = "Response entity status code should match the one from the exception";
    private static final String MSG_INTERNAL_SERVER_ERROR = "HTTP status code should be 500 (Internal Server Error)";
    private static final String MSG_BAD_REQUEST = "HTTP status code should be 400 (Bad Request)";
    private static final String MSG_VALIDATION_ERROR = "Error code should be VALIDATION_ERROR";
    private static final String MSG_VALIDATION_FAILED = "Error message should indicate validation failure";

    /**
     * The GlobalExceptionHandler instance being tested.
     * This is autowired by Spring to inject the actual bean from the application context.
     */
    @Autowired
    private GlobalExceptionHandler globalExceptionHandler;

    /**
     * Tests that handleApplicationException correctly processes an ApplicationException
     * and returns an appropriate error response with the correct status code and error message.
     * This test uses StepVerifier for reactive stream assertions.
     */
    @Test
    void testHandleApplicationException() {
        String errorCode = "APP_ERROR";
        String errorMessage = "Application exception occurred";
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        ApplicationException applicationException = new ApplicationException(errorCode, errorMessage, httpStatus);

        ServerWebExchange exchange = mock(ServerWebExchange.class);
        ServerHttpRequest request = mock(ServerHttpRequest.class);
        when(exchange.getRequest()).thenReturn(request);
        when(request.getURI()).thenReturn(URI.create("http://localhost:8080/some-endpoint"));

        StepVerifier.create(globalExceptionHandler.handleApplicationException(applicationException, exchange))
                .assertNext(responseEntity -> {
                    ErrorResponse errorResponse = responseEntity.getBody();
                    assert errorResponse != null;
                    assertEquals(errorCode, errorResponse.getErrorCode(), MSG_ERROR_CODE);
                    assertEquals(errorMessage, errorResponse.getMessage(), MSG_ERROR_MESSAGE);
                    assertEquals(httpStatus.value(), errorResponse.getStatus(), MSG_HTTP_STATUS);
                    assertEquals("/some-endpoint", errorResponse.getPath(), MSG_PATH);
                    assertEquals(httpStatus, responseEntity.getStatusCode(), MSG_STATUS_CODE);
                })
                .verifyComplete();
    }

    /**
     * Tests that handleApplicationException correctly processes an ApplicationException with a message template
     * and returns an appropriate error response with the formatted message.
     * This test uses StepVerifier for reactive stream assertions.
     */
    @Test
    void testHandleApplicationExceptionWithMessageTemplate() {
        String errorCode = "VALIDATION_ERROR";
        String messageTemplate = "Validation error: %s is invalid";
        String paramValue = "username";
        String expectedMessage = "Validation error: username is invalid";
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;

        ApplicationException applicationException = new ApplicationException(errorCode, messageTemplate, httpStatus, paramValue);

        ServerWebExchange exchange = mock(ServerWebExchange.class);
        ServerHttpRequest request = mock(ServerHttpRequest.class);
        when(exchange.getRequest()).thenReturn(request);
        when(request.getURI()).thenReturn(URI.create("http://localhost:8080/validation-endpoint"));

        StepVerifier.create(globalExceptionHandler.handleApplicationException(applicationException, exchange))
                .assertNext(responseEntity -> {
                    ErrorResponse errorResponse = responseEntity.getBody();
                    assert errorResponse != null;
                    assertEquals(errorCode, errorResponse.getErrorCode(), MSG_ERROR_CODE);
                    assertEquals(expectedMessage, errorResponse.getMessage(), "Error message should be formatted with the parameter value");
                    assertEquals(httpStatus.value(), errorResponse.getStatus(), MSG_HTTP_STATUS);
                    assertEquals("/validation-endpoint", errorResponse.getPath(), MSG_PATH);
                    assertEquals(httpStatus, responseEntity.getStatusCode(), MSG_STATUS_CODE);
                })
                .verifyComplete();
    }

    /**
     * Tests that handleWebClientResponseException correctly processes a WebClientResponseException
     * and returns an appropriate error response with the external service error details.
     * This test uses StepVerifier for reactive stream assertions.
     */
    @Test
    void testHandleWebClientResponseException() {
        String externalErrorMessage = "Service Unavailable";
        WebClientResponseException exception = WebClientResponseException.create(
                HttpStatus.SERVICE_UNAVAILABLE.value(),
                externalErrorMessage,
                null,
                null,
                null
        );

        ServerWebExchange exchange = mock(ServerWebExchange.class);
        ServerHttpRequest request = mock(ServerHttpRequest.class);
        when(exchange.getRequest()).thenReturn(request);
        when(request.getURI()).thenReturn(URI.create("http://localhost:8080/external-api"));

        StepVerifier.create(globalExceptionHandler.handleWebClientResponseException(exception, exchange))
                .assertNext(responseEntity -> {
                    ErrorResponse errorResponse = responseEntity.getBody();
                    assert errorResponse != null;
                    assertEquals("EXTERNAL_SERVICE_ERROR", errorResponse.getErrorCode(), "Error code should be EXTERNAL_SERVICE_ERROR");
                    assertEquals("Error from external service: 503 Service Unavailable", errorResponse.getMessage(), "Error message should include the external service error details");
                    assertEquals(HttpStatus.SERVICE_UNAVAILABLE.value(), errorResponse.getStatus(), MSG_HTTP_STATUS);
                    assertEquals("/external-api", errorResponse.getPath(), MSG_PATH);
                    assertEquals(HttpStatus.SERVICE_UNAVAILABLE, responseEntity.getStatusCode(), MSG_STATUS_CODE);
                })
                .verifyComplete();
    }

    /**
     * Tests that handleResponseStatusException correctly processes a ResponseStatusException
     * and returns an appropriate error response with the reason from the exception.
     * This test uses StepVerifier for reactive stream assertions.
     */
    @Test
    void testHandleResponseStatusException() {
        String reason = "Not Found";
        ResponseStatusException exception = new ResponseStatusException(HttpStatus.NOT_FOUND, reason);

        ServerWebExchange exchange = mock(ServerWebExchange.class);
        ServerHttpRequest request = mock(ServerHttpRequest.class);
        when(exchange.getRequest()).thenReturn(request);
        when(request.getURI()).thenReturn(URI.create("http://localhost:8080/test-endpoint"));

        StepVerifier.create(globalExceptionHandler.handleResponseStatusException(exception, exchange))
                .assertNext(responseEntity -> {
                    ErrorResponse errorResponse = responseEntity.getBody();
                    assert errorResponse != null;
                    assertEquals("HTTP_ERROR", errorResponse.getErrorCode(), "Error code should be HTTP_ERROR");
                    assertEquals(reason, errorResponse.getMessage(), MSG_ERROR_MESSAGE);
                    assertEquals(HttpStatus.NOT_FOUND.value(), errorResponse.getStatus(), MSG_HTTP_STATUS);
                    assertEquals("/test-endpoint", errorResponse.getPath(), MSG_PATH);
                    assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode(), MSG_STATUS_CODE);
                })
                .verifyComplete();
    }

    /**
     * Tests that handleGlobalException correctly processes a generic Exception
     * and returns an appropriate error response with internal server error details.
     * This test uses StepVerifier for reactive stream assertions.
     */
    @Test
    void testHandleGlobalException() {
        String exceptionMessage = "Unexpected error occurred";
        Exception globalException = new Exception(exceptionMessage);

        ServerWebExchange exchange = mock(ServerWebExchange.class);
        ServerHttpRequest request = mock(ServerHttpRequest.class);
        when(exchange.getRequest()).thenReturn(request);
        when(request.getURI()).thenReturn(URI.create("http://localhost:8080/global-endpoint"));

        StepVerifier.create(globalExceptionHandler.handleGlobalException(globalException, exchange))
                .assertNext(responseEntity -> {
                    ErrorResponse errorResponse = responseEntity.getBody();
                    assert errorResponse != null;
                    assertEquals("INTERNAL_SERVER_ERROR", errorResponse.getErrorCode(), "Error code should be INTERNAL_SERVER_ERROR");
                    assertEquals("An unexpected error occurred", errorResponse.getMessage(), "Error message should indicate an unexpected error");
                    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), errorResponse.getStatus(), MSG_INTERNAL_SERVER_ERROR);
                    assertEquals("/global-endpoint", errorResponse.getPath(), MSG_PATH);
                    assertEquals(exceptionMessage, errorResponse.getDetails(), "Error details should contain the exception message");
                    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode(), "Response entity status code should be INTERNAL_SERVER_ERROR");
                })
                .verifyComplete();
    }

    /**
     * Tests that handleThrowableException correctly processes a Throwable
     * and returns an appropriate error response for unhandled errors.
     * This test uses StepVerifier for reactive stream assertions.
     */
    @Test
    void testHandleThrowableException() {
        Throwable throwable = new Throwable("Critical system failure");

        ServerWebExchange exchange = mock(ServerWebExchange.class);
        ServerHttpRequest request = mock(ServerHttpRequest.class);
        when(exchange.getRequest()).thenReturn(request);
        when(request.getURI()).thenReturn(URI.create("http://localhost:8080/critical-error"));

        StepVerifier.create(globalExceptionHandler.handleThrowableException(throwable, exchange))
                .assertNext(responseEntity -> {
                    ErrorResponse errorResponse = responseEntity.getBody();
                    assert errorResponse != null;
                    assertEquals("UNHANDLED_ERROR", errorResponse.getErrorCode(), "Error code should be UNHANDLED_ERROR");
                    assertEquals("An unexpected error occurred", errorResponse.getMessage(), "Error message should indicate an unexpected error");
                    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), errorResponse.getStatus(), MSG_INTERNAL_SERVER_ERROR);
                    assertEquals("/critical-error", errorResponse.getPath(), MSG_PATH);
                    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode(), "Response entity status code should be INTERNAL_SERVER_ERROR");
                })
                .verifyComplete();
    }

    /**
     * Tests that handleSystemException correctly processes a SystemException
     * and returns an appropriate error response with the system error details.
     * This test uses StepVerifier for reactive stream assertions.
     */
    @Test
    void testHandleSystemException() {
        String errorCode = ExceptionConstants.CONFIGURATION_ERROR.getErrorCode();
        String errorMessage = "System configuration error occurred";
        SystemException systemException = new SystemException(errorCode, errorMessage);

        ServerWebExchange exchange = mock(ServerWebExchange.class);
        ServerHttpRequest request = mock(ServerHttpRequest.class);
        when(exchange.getRequest()).thenReturn(request);
        when(request.getURI()).thenReturn(URI.create("http://localhost:8080/system-error"));

        StepVerifier.create(globalExceptionHandler.handleSystemException(systemException, exchange))
                .assertNext(responseEntity -> {
                    ErrorResponse errorResponse = responseEntity.getBody();
                    assert errorResponse != null;
                    assertEquals(errorCode, errorResponse.getErrorCode(), MSG_ERROR_CODE);
                    assertEquals(errorMessage, errorResponse.getMessage(), MSG_ERROR_MESSAGE);
                    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), errorResponse.getStatus(), MSG_INTERNAL_SERVER_ERROR);
                    assertEquals("/system-error", errorResponse.getPath(), MSG_PATH);
                    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode(), "Response entity status code should be INTERNAL_SERVER_ERROR");
                })
                .verifyComplete();
    }

    /**
     * Tests that handleConstraintViolationException correctly processes a ConstraintViolationException
     * and returns an appropriate error response with validation error details.
     * This test uses StepVerifier for reactive stream assertions.
     */
    @Test
    void testHandleConstraintViolationException() {
        // Mock ConstraintViolation
        ConstraintViolation<?> violation = mock(ConstraintViolation.class);
        Path path = mock(Path.class);
        when(path.toString()).thenReturn("username");
        when(violation.getPropertyPath()).thenReturn(path);
        when(violation.getMessage()).thenReturn("must not be blank");

        // Create ConstraintViolationException with the mock violation
        Set<ConstraintViolation<?>> violations = Set.of(violation);
        ConstraintViolationException exception = new ConstraintViolationException("Validation failed", violations);

        // Mock exchange
        ServerWebExchange exchange = mock(ServerWebExchange.class);
        ServerHttpRequest request = mock(ServerHttpRequest.class);
        when(exchange.getRequest()).thenReturn(request);
        when(request.getURI()).thenReturn(URI.create("http://localhost:8080/validation-test"));

        // Test the handler
        StepVerifier.create(globalExceptionHandler.handleConstraintViolationException(exception, exchange))
                .assertNext(responseEntity -> {
                    ErrorResponse errorResponse = responseEntity.getBody();
                    assert errorResponse != null;
                    assertEquals("VALIDATION_ERROR", errorResponse.getErrorCode(), MSG_VALIDATION_ERROR);
                    assertEquals("Validation failed", errorResponse.getMessage(), MSG_VALIDATION_FAILED);
                    assertEquals("username: must not be blank", errorResponse.getDetails(), "Error details should contain the validation constraint violation");
                    assertEquals(HttpStatus.BAD_REQUEST.value(), errorResponse.getStatus(), MSG_BAD_REQUEST);
                    assertEquals("/validation-test", errorResponse.getPath(), MSG_PATH);
                    assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode(), "Response entity status code should be BAD_REQUEST");
                })
                .verifyComplete();
    }

    /**
     * Tests that handleWebExchangeBindException correctly processes a WebExchangeBindException
     * and returns an appropriate error response with field validation error details.
     * This test uses StepVerifier for reactive stream assertions.
     */
    @Test
    void testHandleWebExchangeBindException() {
        // Mock FieldError
        FieldError fieldError = mock(FieldError.class);
        when(fieldError.getField()).thenReturn("email");
        when(fieldError.getDefaultMessage()).thenReturn("must be a valid email address");

        // Mock WebExchangeBindException
        WebExchangeBindException exception = mock(WebExchangeBindException.class);
        when(exception.getBindingResult()).thenReturn(mock(org.springframework.validation.BindingResult.class));
        when(exception.getBindingResult().getFieldErrors()).thenReturn(java.util.List.of(fieldError));

        // Mock exchange
        ServerWebExchange exchange = mock(ServerWebExchange.class);
        ServerHttpRequest request = mock(ServerHttpRequest.class);
        when(exchange.getRequest()).thenReturn(request);
        when(request.getURI()).thenReturn(URI.create("http://localhost:8080/bind-test"));

        // Test the handler
        StepVerifier.create(globalExceptionHandler.handleWebExchangeBindException(exception, exchange))
                .assertNext(responseEntity -> {
                    ErrorResponse errorResponse = responseEntity.getBody();
                    assert errorResponse != null;
                    assertEquals("VALIDATION_ERROR", errorResponse.getErrorCode(), MSG_VALIDATION_ERROR);
                    assertEquals("Validation failed", errorResponse.getMessage(), MSG_VALIDATION_FAILED);
                    assertEquals("email: must be a valid email address", errorResponse.getDetails(), "Error details should contain the field validation error");
                    assertEquals(HttpStatus.BAD_REQUEST.value(), errorResponse.getStatus(), MSG_BAD_REQUEST);
                    assertEquals("/bind-test", errorResponse.getPath(), MSG_PATH);
                    assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode(), "Response entity status code should be BAD_REQUEST");
                })
                .verifyComplete();
    }
}
