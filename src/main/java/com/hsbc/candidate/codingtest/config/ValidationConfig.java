package com.hsbc.candidate.codingtest.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

/**
 * Configuration class for validation.
 * This class configures the validation infrastructure for the application.
 */
@Configuration
public class ValidationConfig {

    /**
     * Creates a MethodValidationPostProcessor bean.
     * This bean enables validation of method parameters and return values.
     *
     * @return a MethodValidationPostProcessor
     */
    @Bean
    public MethodValidationPostProcessor methodValidationPostProcessor() {
        return new MethodValidationPostProcessor();
    }
}
