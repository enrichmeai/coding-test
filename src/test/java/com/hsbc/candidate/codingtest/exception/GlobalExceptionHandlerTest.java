package com.hsbc.candidate.codingtest.exception;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ResponseStatusException;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

import com.hsbc.candidate.codingtest.exception.SystemException;

@SpringBootTest
class GlobalExceptionHandlerTest {

    @Autowired
    private GlobalExceptionHandler globalExceptionHandler;

    @Test
    void testHandleApplicationException() {
        String errorCode = "APP_ERROR";
        String errorMessage = "Application exception occurred";
        ApplicationException applicationException = new ApplicationException(errorCode, errorMessage);
        WebRequest webRequest = mock(WebRequest.class);
        Mockito.when(webRequest.getDescription(false)).thenReturn("uri=/some-endpoint");

        StepVerifier.create(globalExceptionHandler.handleApplicationException(applicationException, webRequest))
                .assertNext(responseEntity -> {
                    ErrorResponse errorResponse = responseEntity.getBody();
                    assertEquals(errorCode, errorResponse.getErrorCode());
                    assertEquals(errorMessage, errorResponse.getMessage());
                    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), errorResponse.getStatus());
                    assertEquals("/some-endpoint", errorResponse.getPath());
                    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
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
        WebRequest webRequest = mock(WebRequest.class);
        Mockito.when(webRequest.getDescription(false)).thenReturn("uri=/external-api");

        StepVerifier.create(globalExceptionHandler.handleWebClientResponseException(exception, webRequest))
                .assertNext(responseEntity -> {
                    ErrorResponse errorResponse = responseEntity.getBody();
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
        WebRequest webRequest = mock(WebRequest.class);
        Mockito.when(webRequest.getDescription(false)).thenReturn("uri=/test-endpoint");

        StepVerifier.create(globalExceptionHandler.handleResponseStatusException(exception, webRequest))
                .assertNext(responseEntity -> {
                    ErrorResponse errorResponse = responseEntity.getBody();
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
        WebRequest webRequest = mock(WebRequest.class);
        Mockito.when(webRequest.getDescription(false)).thenReturn("uri=/global-endpoint");

        StepVerifier.create(globalExceptionHandler.handleGlobalException(globalException, webRequest))
                .assertNext(responseEntity -> {
                    ErrorResponse errorResponse = responseEntity.getBody();
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
        WebRequest webRequest = mock(WebRequest.class);
        Mockito.when(webRequest.getDescription(false)).thenReturn("uri=/critical-error");

        StepVerifier.create(globalExceptionHandler.handleThrowableException(throwable, webRequest))
                .assertNext(responseEntity -> {
                    ErrorResponse errorResponse = responseEntity.getBody();
                    assertEquals("UNHANDLED_ERROR", errorResponse.getErrorCode());
                    assertEquals("An unexpected error occurred", errorResponse.getMessage());
                    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), errorResponse.getStatus());
                    assertEquals("/critical-error", errorResponse.getPath());
                    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
                })
                .verifyComplete();
    }
}
