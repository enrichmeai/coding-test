import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import '@testing-library/jest-dom';
import CityLetterFinder from '../CityLetterFinder';

// Mock the fetch function
global.fetch = jest.fn();

describe('CityLetterFinder Component', () => {
  // Reset mocks before each test
  beforeEach(() => {
    jest.resetAllMocks();
  });

  // Test 1: Component renders correctly
  test('renders the component with initial state', () => {
    render(<CityLetterFinder />);

    // Check if the main elements are rendered
    expect(screen.getByText('Enter a letter to see how many cities start with that letter')).toBeInTheDocument();
    expect(screen.getByTestId('letter-input')).toBeInTheDocument();
    expect(screen.getByTestId('search-button')).toBeInTheDocument();
    expect(screen.getByTestId('clear-button')).toBeInTheDocument();

    // Check that results are not displayed initially
    expect(screen.queryByTestId('city-count-result')).not.toBeInTheDocument();
    expect(screen.queryByTestId('city-list')).not.toBeInTheDocument();
  });

  // Test 2: Input validation - valid input
  test('accepts valid letter input', () => {
    render(<CityLetterFinder />);

    const input = screen.getByTestId('letter-input');
    fireEvent.change(input, { target: { value: 'A' } });

    // No validation error should be displayed
    expect(screen.queryByTestId('validation-error')).not.toBeInTheDocument();
  });

  // Test 3: Input validation - invalid input
  test('shows validation error for invalid input', () => {
    render(<CityLetterFinder />);

    const input = screen.getByTestId('letter-input');
    fireEvent.change(input, { target: { value: '1' } });

    // Validation error should be displayed
    expect(screen.getByTestId('validation-error')).toBeInTheDocument();
  });

  // Test 4: Form submission with valid input
  test('submits form with valid input and fetches data', async () => {
    // Mock successful responses
    (global.fetch as jest.Mock).mockImplementation((url) => {
      if (url.includes('/api/weather/cities/count')) {
        return Promise.resolve({
          ok: true,
          json: () => Promise.resolve({ count: 3 })
        });
      } else if (url.includes('/api/weather/cities')) {
        return Promise.resolve({
          ok: true,
          json: () => Promise.resolve(['Zuwarah', 'Zawiya', 'Zlitan'])
        });
      }
      return Promise.reject(new Error('Not found'));
    });

    render(<CityLetterFinder />);

    // Enter a valid letter and submit the form
    const input = screen.getByTestId('letter-input');
    fireEvent.change(input, { target: { value: 'Z' } });

    const submitButton = screen.getByTestId('search-button');
    fireEvent.click(submitButton);

    // Wait for the API calls to complete
    await waitFor(() => {
      // Check that the results are displayed
      expect(screen.getByTestId('city-count-result')).toBeInTheDocument();
      expect(screen.getByTestId('city-list')).toBeInTheDocument();

      // Check that the city count is displayed
      expect(screen.getByText('3')).toBeInTheDocument();

      // Check that the cities are displayed
      expect(screen.getByText('Zuwarah')).toBeInTheDocument();
      expect(screen.getByText('Zawiya')).toBeInTheDocument();
      expect(screen.getByText('Zlitan')).toBeInTheDocument();
    });
  });

  // Test 5: Error handling - API error
  test('handles API errors correctly', async () => {
    // Mock error response
    (global.fetch as jest.Mock).mockImplementation(() => {
      return Promise.resolve({
        ok: false,
        json: () => Promise.resolve({
          message: 'Internal Server Error',
          errorCode: 'ERR-500',
          status: '500'
        })
      });
    });

    render(<CityLetterFinder />);

    // Enter a valid letter and submit the form
    const input = screen.getByTestId('letter-input');
    fireEvent.change(input, { target: { value: 'Z' } });

    const submitButton = screen.getByTestId('search-button');
    fireEvent.click(submitButton);

    // Wait for the API calls to complete
    await waitFor(() => {
      // Check that the error message is displayed
      expect(screen.getByTestId('error-container')).toBeInTheDocument();
      expect(screen.getByText('Internal Server Error')).toBeInTheDocument();
    });
  });

  // Test 6: Clear button functionality
  test('clear button resets the form', async () => {
    // Mock successful responses
    (global.fetch as jest.Mock).mockImplementation((url) => {
      if (url.includes('/api/weather/cities/count')) {
        return Promise.resolve({
          ok: true,
          json: () => Promise.resolve({ count: 3 })
        });
      } else if (url.includes('/api/weather/cities')) {
        return Promise.resolve({
          ok: true,
          json: () => Promise.resolve(['Zuwarah', 'Zawiya', 'Zlitan'])
        });
      }
      return Promise.reject(new Error('Not found'));
    });

    render(<CityLetterFinder />);

    // Enter a valid letter and submit the form
    const input = screen.getByTestId('letter-input');
    fireEvent.change(input, { target: { value: 'Z' } });

    const submitButton = screen.getByTestId('search-button');
    fireEvent.click(submitButton);

    // Wait for the API calls to complete
    await waitFor(() => {
      expect(screen.getByTestId('city-count-result')).toBeInTheDocument();
    });

    // Click the clear button
    const clearButton = screen.getByTestId('clear-button');
    fireEvent.click(clearButton);

    // Check that the form is reset
    expect(input).toHaveValue('');
    expect(screen.queryByTestId('city-count-result')).not.toBeInTheDocument();
    expect(screen.queryByTestId('city-list')).not.toBeInTheDocument();
  });

  // Test 7: Loading state
  test('shows loading state during API calls', async () => {
    // Mock delayed response
    (global.fetch as jest.Mock).mockImplementation(() => {
      return new Promise(resolve => {
        setTimeout(() => {
          resolve({
            ok: true,
            json: () => Promise.resolve({ count: 3 })
          });
        }, 100);
      });
    });

    render(<CityLetterFinder />);

    // Enter a valid letter and submit the form
    const input = screen.getByTestId('letter-input');
    fireEvent.change(input, { target: { value: 'Z' } });

    const submitButton = screen.getByTestId('search-button');
    fireEvent.click(submitButton);

    // Check that loading state is displayed
    expect(screen.getByTestId('loading-indicator')).toBeInTheDocument();

    // Wait for the API call to complete
    await waitFor(() => {
      expect(screen.queryByTestId('loading-indicator')).not.toBeInTheDocument();
    }, { timeout: 200 });
  });
});
