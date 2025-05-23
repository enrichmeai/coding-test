import React, { useState, useCallback } from 'react';
import './CityLetterFinder.css';

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

interface ApiError {
  message: string;
  errorData?: any;
}

type ApiEndpoint = 'count' | 'list';

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

  // Generic fetch function to reduce code duplication
  const fetchData = useCallback(async <T,>(
    endpoint: ApiEndpoint,
    letter: string,
    errorMessage: string
  ): Promise<T | null> => {
    const url = endpoint === 'count'
      ? `/api/weather/cities/count?letter=${letter}`
      : `/api/weather/cities?letter=${letter}`;

    try {
      const response = await fetch(url);

      if (!response.ok) {
        const errorData = await response.json();
        throw {
          message: `HTTP error! Status: ${response.status}`,
          errorData
        } as ApiError;
      }

      return await response.json() as T;
    } catch (err: any) {
      const apiError = err as ApiError;
      if (apiError.errorData) {
        setError({
          message: apiError.errorData.message || errorMessage,
          errorCode: apiError.errorData.errorCode,
          status: apiError.errorData.status,
          path: apiError.errorData.path,
          timestamp: apiError.errorData.timestamp,
          details: apiError.errorData.details
        });
      } else {
        setError({
          message: `${errorMessage}: ${apiError.message || 'Unknown error'}`
        });
      }
      return null;
    }
  }, []);

  // Function to handle form submission
  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!validateInput(letter)) {
      return;
    }

    setLoading(true);
    setError(null);
    setCityCount(null);
    setCities([]);

    try {
      // Use Promise.all to fetch data in parallel
      const [countData, citiesData] = await Promise.all([
        fetchData<CityCount>('count', letter, 'Failed to fetch city count'),
        fetchData<string[]>('list', letter, 'Failed to fetch city list')
      ]);

      if (countData) {
        setCityCount(countData.count);
      }

      if (citiesData) {
        setCities(citiesData);
      }
    } catch (err) {
      console.error('Error fetching data:', err);
    } finally {
      setLoading(false);
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
    <div className="city-letter-finder" data-testid="city-letter-finder">
      <p>Enter a letter to see how many cities start with that letter</p>

      <form onSubmit={handleSubmit} className="input-container" aria-label="City search form">
        <input
          type="text"
          value={letter}
          onChange={handleInputChange}
          maxLength={1}
          placeholder="Enter a letter"
          className="letter-input"
          aria-label="Letter input"
          aria-invalid={!!validationError}
          aria-describedby={validationError ? "validation-error" : undefined}
          data-testid="letter-input"
        />
        <button
          type="submit"
          className="search-button"
          aria-label="Search"
          disabled={loading}
          data-testid="search-button"
        >
          Search
        </button>
        <button
          type="button"
          onClick={handleClear}
          className="clear-button"
          aria-label="Clear form"
          data-testid="clear-button"
        >
          Clear
        </button>
      </form>

      {validationError && (
        <div
          className="validation-message error"
          id="validation-error"
          role="alert"
          data-testid="validation-error"
        >
          {validationError}
        </div>
      )}

      <div className="result-container">
        {loading && (
          <div className="loading" role="status" aria-live="polite" data-testid="loading-indicator">
            <span>Loading...</span>
          </div>
        )}

        {cityCount !== null && !error && (
          <div className="result" data-testid="city-count-result">
            <p>
              Number of cities starting with "{letter}":
              <span className="city-count" aria-live="polite">{cityCount}</span>
            </p>
          </div>
        )}

        {cities.length > 0 && !error && (
          <div className="city-list" data-testid="city-list">
            <h3>Cities:</h3>
            <ul aria-label={`Cities starting with ${letter}`}>
              {cities.map((city) => (
                <li key={city}>{city}</li>
              ))}
            </ul>
          </div>
        )}

        {error && (
          <div
            className="error-container"
            role="alert"
            aria-live="assertive"
            data-testid="error-container"
          >
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

// Use React.memo to prevent unnecessary re-renders
export default React.memo(CityLetterFinder);
