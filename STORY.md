# City Counter Application - User Story

## Story Title
As a user, I want to find out how many cities start with a specific letter so that I can learn about city name distributions.

## Description
The City Counter application allows users to enter a letter and see the number of cities beginning with that letter from the OpenWeatherMap API. This helps users understand the distribution of city names and explore cities that start with a particular letter.

## Acceptance Criteria
1. **Input Validation**
   - The application should accept a single letter (A-Z or a-z) as input
   - The application should provide validation feedback if the input is not a single letter
   - The application should allow the user to clear the input and start over

2. **City Counting Functionality**
   - When a valid letter is entered, the application should display the count of cities starting with that letter
   - The application should display a list of the matching city names
   - Example: Entering "y" should show 1 as the count (for "Yafran")
   - Example: Entering "z" should show 3 as the count (for "Zuwarah", "Zawiya", "Zlitan")

3. **User Interface**
   - The UI should be intuitive and easy to use
   - The UI should provide clear feedback during the search process (loading indicator)
   - The UI should display error messages if the API request fails
   - The UI should be responsive and work on different screen sizes

4. **Technical Requirements**
   - The backend should be built with Java 17 or higher
   - The frontend can be plain HTML/JS or a framework like React
   - The application should fetch data from the OpenWeatherMap API
   - The application should handle API errors gracefully

5. **Documentation**
   - The project should include a README.md file explaining how to build and run the application
   - The code should be well-documented with comments explaining complex logic
   - The project should be uploaded to a git repository

## Technical Notes
- API Endpoint: https://samples.openweathermap.org/data/2.5/box/city?bbox=12,32,15,37,10&appid=b6907d289e10d714a6e88b30761fae22
- The API returns city data in JSON format with the following structure:
  ```json
  {
    "cod": "200",
    "calctime": 0.3107,
    "cnt": 15,
    "name": "Test data",
    "list": [
      {
        "id": 2208791,
        "name": "Yafran",
        "coord": {
          "lon": 12.52859,
          "lat": 32.06329
        },
        "main": {
          "temp": 9.68,
          "temp_min": 9.681,
          "temp_max": 9.681,
          "pressure": 961.02,
          "sea_level": 1036.82,
          "grnd_level": 961.02,
          "humidity": 85
        },
        "dt": 1485784982,
        "wind": {
          "speed": 3.96,
          "deg": 356.5
        },
        "rain": {
          "3h": 0.255
        },
        "clouds": {
          "all": 88
        },
        "weather": [
          {
            "id": 500,
            "main": "Rain",
            "description": "light rain",
            "icon": "10d"
          }
        ]
      }
    ]
  }
  ```
