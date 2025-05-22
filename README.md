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

## Technology Stack

- Backend: Java 17, Spring Boot 3.4.5
- Frontend: HTML, CSS, JavaScript
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
- `src/main/resources/static` - Frontend files (HTML, CSS, JavaScript)
- `src/test/java` - Backend test code
- `src/test/resources/static` - Frontend test code
- `STORY.md` - User story with detailed acceptance criteria

## UI Features

The UI has been improved with the following features:

- Real-time input validation with visual feedback
- Clear button to reset the form
- Enhanced error handling and display
- Improved visual design with better spacing and colors

## Testing

### Backend Tests

Backend tests are written using JUnit and can be run with Gradle:

```bash
./gradlew test
```

### Frontend Tests

Frontend tests are written using Jest and can be run with npm:

```bash
npm test
```

See `src/test/resources/static/README.md` for more details on setting up and running the frontend tests.

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
