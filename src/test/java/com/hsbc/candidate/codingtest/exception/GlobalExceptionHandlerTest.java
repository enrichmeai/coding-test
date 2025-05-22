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

@SpringBootTest
class GlobalExceptionHandlerTest {

    @Autowired
    private GlobalExceptionHandler globalExceptionHandler;

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
                    assertEquals(errorCode, errorResponse.getErrorCode());
                    assertEquals(errorMessage, errorResponse.getMessage());
                    assertEquals(httpStatus.value(), errorResponse.getStatus());
                    assertEquals("/some-endpoint", errorResponse.getPath());
                    assertEquals(httpStatus, responseEntity.getStatusCode());
                })
                .verifyComplete();
    }

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
                    assertEquals(errorCode, errorResponse.getErrorCode());
                    assertEquals(expectedMessage, errorResponse.getMessage());
                    assertEquals(httpStatus.value(), errorResponse.getStatus());
                    assertEquals("/validation-endpoint", errorResponse.getPath());
                    assertEquals(httpStatus, responseEntity.getStatusCode());
                })
                .verifyComplete();
    }

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
                    assertEquals("EXTERNAL_SERVICE_ERROR", errorResponse.getErrorCode());
                    assertEquals("Error from external service: 503 Service Unavailable", errorResponse.getMessage());
                    assertEquals(HttpStatus.SERVICE_UNAVAILABLE.value(), errorResponse.getStatus());
                    assertEquals("/external-api", errorResponse.getPath());
                    assertEquals(HttpStatus.SERVICE_UNAVAILABLE, responseEntity.getStatusCode());
                })
                .verifyComplete();
    }

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
                    assertEquals("HTTP_ERROR", errorResponse.getErrorCode());
                    assertEquals(reason, errorResponse.getMessage());
                    assertEquals(HttpStatus.NOT_FOUND.value(), errorResponse.getStatus());
                    assertEquals("/test-endpoint", errorResponse.getPath());
                    assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
                })
                .verifyComplete();
    }

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
                    assertEquals("INTERNAL_SERVER_ERROR", errorResponse.getErrorCode());
                    assertEquals("An unexpected error occurred", errorResponse.getMessage());
                    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), errorResponse.getStatus());
                    assertEquals("/global-endpoint", errorResponse.getPath());
                    assertEquals(exceptionMessage, errorResponse.getDetails());
                    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
                })
                .verifyComplete();
    }

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
                    assertEquals("UNHANDLED_ERROR", errorResponse.getErrorCode());
                    assertEquals("An unexpected error occurred", errorResponse.getMessage());
                    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), errorResponse.getStatus());
                    assertEquals("/critical-error", errorResponse.getPath());
                    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
                })
                .verifyComplete();
    }

    @Test
    void testHandleSystemException() {
        String errorCode = SystemException.CONFIGURATION_ERROR;
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
                    assertEquals(errorCode, errorResponse.getErrorCode());
                    assertEquals(errorMessage, errorResponse.getMessage());
                    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), errorResponse.getStatus());
                    assertEquals("/system-error", errorResponse.getPath());
                    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
                })
                .verifyComplete();
    }

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
                    assertEquals("VALIDATION_ERROR", errorResponse.getErrorCode());
                    assertEquals("Validation failed", errorResponse.getMessage());
                    assertEquals("username: must not be blank", errorResponse.getDetails());
                    assertEquals(HttpStatus.BAD_REQUEST.value(), errorResponse.getStatus());
                    assertEquals("/validation-test", errorResponse.getPath());
                    assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
                })
                .verifyComplete();
    }

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
                    assertEquals("VALIDATION_ERROR", errorResponse.getErrorCode());
                    assertEquals("Validation failed", errorResponse.getMessage());
                    assertEquals("email: must be a valid email address", errorResponse.getDetails());
                    assertEquals(HttpStatus.BAD_REQUEST.value(), errorResponse.getStatus());
                    assertEquals("/bind-test", errorResponse.getPath());
                    assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
                })
                .verifyComplete();
    }
}
