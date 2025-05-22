package com.hsbc.candidate.codingtest.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Test class for the ValidationConfig class.
 * This class contains unit tests for the bean creation of the methodValidationPostProcessor method.
 */
@SpringBootTest
public class ValidationConfigTest {

    @Autowired
    private ValidationConfig validationConfig;

    /**
     * Test to verify that the methodValidationPostProcessor bean is created successfully
     * and is not null.
     */
    @Test
    void testMethodValidationPostProcessorBeanCreation() {
        MethodValidationPostProcessor postProcessor = validationConfig.methodValidationPostProcessor();
        assertNotNull(postProcessor, "The MethodValidationPostProcessor bean should not be null");
    }
}
