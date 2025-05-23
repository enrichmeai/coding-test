# City Letter Finder Application

This application allows users to enter a letter and see the number of cities beginning with that letter from the OpenWeatherMap API.

## Features

- Enter a single letter to search for cities
- View the count of cities starting with the entered letter
- See a list of matching city names
- Responsive UI design

## Prerequisites

- Java 17 or higher
- Gradle (or use the included Gradle wrapper)
- Internet connection to access the OpenWeatherMap API

## Building the Project

You can build the project using the Gradle wrapper:

```bash
./gradlew build
```

This will compile the code, run tests, and create an executable JAR file.

## Running the Application

After building, you can run the application using:

```bash
./gradlew bootRun
```

Or you can run the JAR file directly:

```bash
java -jar build/libs/city-letter-finder-0.0.1-SNAPSHOT.jar
```

The application will start and be available at http://localhost:8080

## How to Use

1. Open your web browser and navigate to http://localhost:8080
2. Enter a single letter in the input field
3. Click the "Search" button or press Enter
4. The application will display:
   - The number of cities starting with the entered letter
   - A list of the matching city names

For example:
- Entering "y" will show 1 city (Yafran)
- Entering "z" will show 3 cities (Zuwarah, Zawiya, Zlitan)

## API Endpoints

The application provides the following REST API endpoints:

- `GET /api/weather` - Returns all weather data
- `GET /api/weather/cities/count?letter={letter}` - Returns the count of cities starting with the given letter
- `GET /api/weather/cities?letter={letter}` - Returns a list of city names starting with the given letter

### Postman Collection

A Postman collection is available to help you test the API endpoints. The collection is located in the `postman` directory of the project.

To use the collection:
1. Import the file `postman/weather-api-postman-collection.json` into Postman
2. Ensure the application is running
3. Execute the requests in the collection to test the API endpoints

## Technology Stack

- Backend: Java 17, Spring Boot 3.4.5
- Frontend: React with TypeScript
- Build Tool: Gradle
- Testing: JUnit (backend), Jest (frontend)
- Code Quality: Checkstyle, PMD
- Additional Dependencies:
  - Netty DNS resolver for MacOS (for proper DNS resolution on MacOS systems)
    - Supports both Intel (x86_64) and Apple Silicon (M1/M2/M3) architectures

## Project Structure

- `src/main/java/com/hsbc/candidate/codingtest` - Java source code
  - `client` - API client for external services
  - `config` - Configuration classes
  - `controller` - REST API controllers
  - `exception` - Exception handling classes
  - `model` - Data models
  - `service` - Business logic
- `src/main/resources/static` - Static resources for the frontend
- `src/main/webapp` - React TypeScript frontend source code
  - `components` - React components
  - `App.tsx` - Main React component
  - `index.tsx` - React entry point
- `src/test/java` - Backend test code
- `src/test/resources` - Test resources and configuration
- `STORY.md` - User story with detailed acceptance criteria
- `tsconfig.json` - TypeScript configuration
- `.env` - Environment variables for React

## Architecture

The application follows a layered architecture pattern with clear separation of concerns:

### Layered Architecture

1. **Presentation Layer (Controllers)**
   - `WeatherController`: Handles HTTP requests, validates input, and returns appropriate responses
   - Uses reactive programming with Spring WebFlux for non-blocking request handling
   - Endpoints are mapped to specific service methods

2. **Service Layer**
   - `WeatherService`: Contains business logic for processing weather data
   - Filters cities based on starting letter
   - Transforms data from external API into application-specific format
   - Handles validation and error cases

3. **Client Layer**
   - `WeatherServiceClient`: Communicates with external OpenWeather API
   - Uses WebClient for reactive HTTP requests
   - Implements retry logic for handling transient failures

4. **Model Layer**
   - Domain models representing weather data (City, Weather, etc.)
   - Data transfer objects for API responses

### Exception Handling

The application implements a comprehensive exception handling strategy:

- **GlobalExceptionHandler**: Central exception handler that processes all exceptions
  - Maps exceptions to appropriate HTTP status codes
  - Provides consistent error response format
  - Logs exceptions with appropriate severity levels

- **Exception Types**:
  - `ApplicationException`: Base class for application-specific exceptions
  - `SystemException`: For system-level errors
  - `WeatherServiceException`: For errors related to weather service operations
  - Standard Spring exceptions (WebExchangeBindException, etc.)

- **Error Response Format**:
  - Error code (application-specific identifier)
  - Error message (user-friendly description)
  - HTTP status code
  - Timestamp
  - Request path
  - Additional details (when available)

### Configuration

- **WeatherServiceWebclientConfig**: Configures the WebClient for API communication
- **OpenWeatherApiBboxConfig**: Configures the bounding box parameters for the OpenWeather API
- **ValidationConfig**: Sets up validation for request parameters

### Component Interaction Flow

1. Client sends HTTP request to a controller endpoint
2. Controller validates input and delegates to service layer
3. Service layer processes the request, using the client layer when needed
4. Client layer communicates with external API
5. Service layer transforms and filters the response
6. Controller returns the processed data to the client
7. If errors occur at any stage, they're caught by the GlobalExceptionHandler

### Reactive Programming

The application uses reactive programming (Project Reactor) for:
- Non-blocking I/O operations
- Efficient resource utilization
- Handling backpressure
- Composing asynchronous operations

## UI Features

The UI has been implemented with React and TypeScript, providing the following features:

- Component-based architecture for better maintainability
- Strong typing with TypeScript for improved code quality and developer experience
- Real-time input validation with visual feedback
- Clear button to reset the form
- Enhanced error handling and display
- Improved visual design with better spacing and colors
- State management using React hooks

### Current Implementation

The UI is implemented using React with TypeScript, which provides several advantages:

- **Type Safety**: TypeScript adds static type checking to catch errors during development
- **Component-Based Architecture**: React's component-based approach makes the UI more modular and easier to maintain
- **State Management**: Efficient state management with React hooks
- **Developer Experience**: Improved developer experience with hot reloading and better debugging tools
- **Performance**: Virtual DOM for efficient rendering and better performance
- **Ecosystem**: Access to a rich ecosystem of libraries and tools

The React application is built and embedded within the Spring Boot application, allowing for a single deployment while still benefiting from modern frontend development practices.

### Building the React Frontend

To build the React frontend:

```bash
# Install dependencies
npm install

# Build the React application and copy to Spring Boot static resources
npm run build:react
```

This will compile the TypeScript code, bundle the React application, and copy the built files to the Spring Boot static resources directory.

### Development Workflow

For frontend development, you can use the React development server:

```bash
npm start
```

This will start the React development server with hot reloading at http://localhost:3000. The development server is configured to proxy API requests to the Spring Boot backend at http://localhost:8080.

### Future Considerations

As the application grows, consider:

- Adding state management libraries like Redux or Zustand for more complex state requirements
- Implementing more advanced routing with React Router
- Adding more comprehensive testing with React Testing Library
- Exploring server-side rendering or static site generation for improved performance

These enhancements would further improve the application's maintainability, performance, and user experience.

## Testing

### Backend Tests

Backend tests are written using JUnit and can be run with Gradle:

```bash
./gradlew test
```

### Frontend Tests

Frontend tests are written using Jest and React Testing Library and can be run with npm:

```bash
npm test
```

The Jest configuration is set up in `jest.config.js` to run tests for the React components. The tests are located in the `__tests__` directory within the components directory.

A custom setup file (`src/test/setup/setupTests.js`) is used to patch the React Testing Library to use `React.act` instead of the deprecated `ReactDOMTestUtils.act`, eliminating deprecation warnings during test execution.

The frontend tests cover:

- **Component Rendering**: Tests that components render correctly with their initial state
- **User Interactions**: Tests for input validation, form submission, and button clicks
- **API Integration**: Tests that API calls are made correctly and results are displayed
- **Error Handling**: Tests that error states are handled and displayed correctly
- **Loading States**: Tests that loading indicators are shown during API calls

For example, the `CityLetterFinder` component has tests for:
- Initial rendering
- Input validation for valid and invalid letters
- Form submission with API calls
- Error handling for API errors
- Clear button functionality
- Loading state during API calls

### Integration Tests

The project includes integration tests that verify the application works correctly end-to-end:

- **Postman Tests**: API tests using Newman (Postman's command-line runner)

#### Running Integration Tests

There are three ways to run integration tests:

**Option 1: Run integration tests without building first**

If you've already built the application and just want to run the tests:

```bash
# Run all integration tests
./gradlew integrationTest

# Run only Postman tests
./gradlew runPostmanIntegrationTest
```

Each of these tasks will:
1. Start the Spring Boot application
2. Run the specified tests
3. Stop the application

**Option 2: Build and run integration tests in one command**

If you want to ensure the application is built before running tests:

```bash
# Build and run all integration tests
./gradlew buildAndRunIntegrationTests

# Build and run only Postman tests
./gradlew buildAndRunPostmanTests
```

Each of these tasks will:
1. Build the application
2. Start the Spring Boot application
3. Run the specified tests
4. Stop the application

**Option 3: Run individual test tasks**

If you want more control over the test execution, you can run the individual test tasks directly:

```bash
# Start the application
./gradlew startApp

# Run Postman tests
./gradlew runPostmanTests

# Stop the application
./gradlew stopApp
```

This option gives you the flexibility to:
- Run tests against an already running application
- Run tests in a specific order
- Run tests multiple times without restarting the application
- Manually inspect the application state between tests

#### Prerequisites for Integration Tests

- Node.js and npm (for running Newman/Postman tests)
- Internet connection (for accessing external APIs)

## Code Quality

The project uses the following code quality tools:

### Checkstyle

Checkstyle is used to enforce coding standards. You can run Checkstyle with:

```bash
./gradlew checkstyleMain  # For main source code
./gradlew checkstyleTest  # For test source code
```

### PMD

PMD is used to detect potential code issues. You can run PMD with:

```bash
./gradlew pmdMain  # For main source code
./gradlew pmdTest  # For test source code
```

For more details on the code quality tools configuration, see `README-code-quality.md`.

## Data Source

The application fetches data from the OpenWeatherMap API:
https://samples.openweathermap.org/data/2.5/box/city?bbox=12,32,15,37,10&appid=b6907d289e10d714a6e88b30761fae22
