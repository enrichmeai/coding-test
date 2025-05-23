import React, { useState } from 'react';
import './CityLetterFinder.css';

// Define TypeScript interfaces for our data structures
interface CityCount {
  count: number;
}

interface ErrorResponse {
  message: string;
  errorCode?: string;
  status?: string;
  path?: string;
  timestamp?: string;
  details?: string;
}

const CityLetterFinder: React.FC = () => {
  // State variables with TypeScript types
  const [letter, setLetter] = useState<string>('');
  const [cityCount, setCityCount] = useState<number | null>(null);
  const [cities, setCities] = useState<string[]>([]);
  const [loading, setLoading] = useState<boolean>(false);
  const [error, setError] = useState<ErrorResponse | null>(null);
  const [validationError, setValidationError] = useState<string>('');

  // Function to validate input
  const validateInput = (input: string): boolean => {
    if (!input) {
      setValidationError('Please enter a letter');
      return false;
    }

    if (!isLetter(input)) {
      setValidationError('Please enter a valid letter (A-Z or a-z)');
      return false;
    }

    setValidationError('');
    return true;
  };

  // Function to check if input is a letter
  const isLetter = (str: string): boolean => {
    return str.length === 1 && /[a-zA-Z]/.test(str);
  };

  // Function to handle input change
  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const value = e.target.value;
    setLetter(value);

    if (value) {
      validateInput(value);
    } else {
      setValidationError('');
    }
  };

  // Function to handle form submission
  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();

    if (!validateInput(letter)) {
      return;
    }

    setLoading(true);
    setError(null);
    setCityCount(null);
    setCities([]);

    // Fetch city count
    fetchCityCount(letter);

    // Fetch city list
    fetchCityList(letter);
  };

  // Function to fetch city count
  const fetchCityCount = async (letter: string) => {
    try {
      const response = await fetch(`/api/weather/cities/count?letter=${letter}`);

      if (!response.ok) {
        const errorData = await response.json();
        throw { message: `HTTP error! Status: ${response.status}`, errorData };
      }

      const data: CityCount = await response.json();
      setCityCount(data.count);
    } catch (err: any) {
      if (err.errorData) {
        setError({
          message: err.errorData.message || 'Failed to fetch city count',
          errorCode: err.errorData.errorCode,
          status: err.errorData.status,
          path: err.errorData.path,
          timestamp: err.errorData.timestamp,
          details: err.errorData.details
        });
      } else {
        setError({ message: `Failed to fetch city count: ${err.message}` });
      }
    } finally {
      setLoading(false);
    }
  };

  // Function to fetch city list
  const fetchCityList = async (letter: string) => {
    try {
      const response = await fetch(`/api/weather/cities?letter=${letter}`);

      if (!response.ok) {
        const errorData = await response.json();
        throw { message: `HTTP error! Status: ${response.status}`, errorData };
      }

      const data: string[] = await response.json();
      setCities(data);
    } catch (err: any) {
      if (err.errorData) {
        setError({
          message: err.errorData.message || 'Failed to fetch city list',
          errorCode: err.errorData.errorCode,
          status: err.errorData.status,
          path: err.errorData.path,
          timestamp: err.errorData.timestamp,
          details: err.errorData.details
        });
      } else {
        setError({ message: `Failed to fetch city list: ${err.message}` });
      }
    }
  };

  // Function to clear the form
  const handleClear = () => {
    setLetter('');
    setCityCount(null);
    setCities([]);
    setError(null);
    setValidationError('');
  };

  return (
    <div className="city-letter-finder">
      <p>Enter a letter to see how many cities start with that letter</p>

      <form onSubmit={handleSubmit} className="input-container">
        <input
          type="text"
          value={letter}
          onChange={handleInputChange}
          maxLength={1}
          placeholder="Enter a letter"
          className="letter-input"
        />
        <button type="submit" className="search-button">Search</button>
        <button type="button" onClick={handleClear} className="clear-button">Clear</button>
      </form>

      {validationError && (
        <div className="validation-message error">{validationError}</div>
      )}

      <div className="result-container">
        {loading && <div className="loading">Loading...</div>}

        {cityCount !== null && !error && (
          <div className="result">
            <p>Number of cities starting with "{letter}": <span className="city-count">{cityCount}</span></p>
          </div>
        )}

        {cities.length > 0 && !error && (
          <div className="city-list">
            <h3>Cities:</h3>
            <ul>
              {cities.map((city, index) => (
                <li key={index}>{city}</li>
              ))}
            </ul>
          </div>
        )}

        {error && (
          <div className="error-container">
            <h3>Error</h3>
            <p><strong>Message:</strong> {error.message}</p>
            {error.errorCode && <p><strong>Code:</strong> {error.errorCode}</p>}
            {error.status && <p><strong>Status:</strong> {error.status}</p>}
            {error.details && (
              <div className="error-details">
                <p><strong>Details:</strong> {error.details}</p>
              </div>
            )}
            {error.path && <p><strong>Path:</strong> {error.path}</p>}
            {error.timestamp && (
              <p><strong>Time:</strong> {new Date(error.timestamp).toLocaleString()}</p>
            )}
          </div>
        )}
      </div>
    </div>
  );
};

export default CityLetterFinder;
