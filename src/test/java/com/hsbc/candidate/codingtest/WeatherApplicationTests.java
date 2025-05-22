package com.hsbc.candidate.codingtest;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Integration test for the WeatherApplication.
 * This test verifies that the Spring application context loads successfully.
 */
@SpringBootTest
class WeatherApplicationTests {

    /**
     * Tests that the application context loads successfully.
     * This test will fail if there are any issues with bean configuration or component scanning.
     * No explicit assertions are needed as the test passes if the context loads without exceptions.
     */
    @Test
    @SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert") // This test passes if no exceptions are thrown
    void contextLoads() {
        // No assertions needed - test passes if application context loads successfully
    }

}
