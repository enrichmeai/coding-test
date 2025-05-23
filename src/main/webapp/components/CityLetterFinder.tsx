import React from 'react';
import './CityLetterFinder.css';
import { weatherApiClient, ErrorResponse } from '../api/WeatherApiClient';

// Define the component's state interface
interface CityLetterFinderState {
  letter: string;
  cityCount: number | null;
  cities: string[];
  loading: boolean;
  error: ErrorResponse | null;
  validationError: string;
}

// Define the component's props interface (empty in this case)
interface CityLetterFinderProps {}

class CityLetterFinder extends React.Component<CityLetterFinderProps, CityLetterFinderState> {
  constructor(props: CityLetterFinderProps) {
    super(props);

    // Initialize state
    this.state = {
      letter: '',
      cityCount: null,
      cities: [],
      loading: false,
      error: null,
      validationError: ''
    };

    // Bind methods to this instance
    this.handleInputChange = this.handleInputChange.bind(this);
    this.handleSubmit = this.handleSubmit.bind(this);
    this.handleClear = this.handleClear.bind(this);
    this.validateInput = this.validateInput.bind(this);
  }

  // Function to validate input
  validateInput(input: string): boolean {
    if (!input) {
      this.setState({ validationError: 'Please enter a letter' });
      return false;
    }

    if (!this.isLetter(input)) {
      this.setState({ validationError: 'Please enter a valid letter (A-Z or a-z)' });
      return false;
    }

    this.setState({ validationError: '' });
    return true;
  }

  // Function to check if input is a letter
  isLetter(str: string): boolean {
    return str.length === 1 && /[a-zA-Z]/.test(str);
  }

  // Function to handle input change
  handleInputChange(e: React.ChangeEvent<HTMLInputElement>) {
    const value = e.target.value;
    this.setState({ letter: value });

    if (value) {
      this.validateInput(value);
    } else {
      this.setState({ validationError: '' });
    }
  }


  // Function to handle form submission
  async handleSubmit(e: React.FormEvent) {
    e.preventDefault();

    if (!this.validateInput(this.state.letter)) {
      return;
    }

    this.setState({
      loading: true,
      error: null,
      cityCount: null,
      cities: []
    });

    try {
      // Use Promise.all to fetch data in parallel
      const [countResult, citiesResult] = await Promise.all([
        weatherApiClient.getCityCount(this.state.letter),
        weatherApiClient.getCityList(this.state.letter)
      ]);

      // Handle count result
      if (countResult.error) {
        this.setState({ error: countResult.error });
      } else if (countResult.count !== null) {
        this.setState({ cityCount: countResult.count });
      }

      // Handle cities result
      if (citiesResult.error && !this.state.error) {
        this.setState({ error: citiesResult.error });
      } else if (citiesResult.cities) {
        this.setState({ cities: citiesResult.cities });
      }
    } catch (err) {
      console.error('Error fetching data:', err);
      this.setState({
        error: { message: 'An unexpected error occurred while fetching data.' }
      });
    } finally {
      this.setState({ loading: false });
    }
  }

  // Function to clear the form
  handleClear() {
    this.setState({
      letter: '',
      cityCount: null,
      cities: [],
      error: null,
      validationError: ''
    });
  }

  render() {
    const { letter, cityCount, cities, loading, error, validationError } = this.state;

    return (
      <div className="city-letter-finder" data-testid="city-letter-finder">
        <p>Enter a letter to see how many cities start with that letter</p>

        <form onSubmit={this.handleSubmit} className="input-container" aria-label="City search form">
          <input
            type="text"
            value={letter}
            onChange={this.handleInputChange}
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
            onClick={this.handleClear}
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
  }
}

export default CityLetterFinder;
