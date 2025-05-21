# UI Tests for City Counter Application

This directory contains tests for the UI components of the City Counter application.

## Test Files

- `script.test.js`: Tests for the JavaScript functionality in `script.js`

## Running the Tests

These tests are written using Jest syntax. To run them, you'll need to set up Jest in the project:

1. Install Node.js and npm if you don't have them already
2. Initialize npm in the project root:
   ```bash
   npm init -y
   ```
3. Install Jest:
   ```bash
   npm install --save-dev jest
   ```
4. Add a test script to package.json:
   ```json
   {
     "scripts": {
       "test": "jest"
     }
   }
   ```
5. Run the tests:
   ```bash
   npm test
   ```

## Test Structure

The tests are organized by function:

- `isLetter`: Tests for the function that validates if input is a letter
- `validateInput`: Tests for the function that provides validation feedback
- `clearForm`: Tests for the function that clears the form

## Mocking

The tests use Jest's mocking capabilities to mock:

- DOM elements
- Event listeners
- The fetch API

This allows testing the JavaScript functionality without a browser environment.

## Adding New Tests

To add new tests:

1. Create a new test file or add to an existing one
2. Mock any required DOM elements or APIs
3. Write test cases using Jest's `describe` and `test` functions
4. Run the tests to verify they pass

## Integration with Backend Tests

These UI tests complement the existing backend tests in:

- `src/test/java/com/example/demo/controller/WeatherControllerTest.java`
- `src/test/java/com/example/demo/service/WeatherServiceTest.java`
- `src/test/java/com/example/demo/client/WeatherServiceClientTest.java`

Together, they provide comprehensive test coverage for the application.
