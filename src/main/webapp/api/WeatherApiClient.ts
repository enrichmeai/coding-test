/**
 * WeatherApiClient.ts
 *
 * This file contains the API client for interacting with the weather API endpoints
 * and the type definitions for the API responses.
 */

// Type definitions for API responses
export interface CityCount {
  count: number;
}

export interface ErrorResponse {
  message: string;
  errorCode?: string;
  status?: string;
  path?: string;
  timestamp?: string;
  details?: string;
}

export interface ApiError {
  message: string;
  errorData?: any;
}

// API endpoints type
export type ApiEndpoint = 'count' | 'list';

/**
 * WeatherApiClient class for handling API interactions
 */
export class WeatherApiClient {
  /**
   * Fetches data from the weather API
   *
   * @param endpoint - The API endpoint to fetch data from ('count' or 'list')
   * @param letter - The letter to filter cities by
   * @param errorMessage - The error message to display if the request fails
   * @returns A promise that resolves to the API response or null if an error occurs
   */
  public async fetchData<T>(
    endpoint: ApiEndpoint,
    letter: string,
    errorMessage: string
  ): Promise<{ data: T | null; error: ErrorResponse | null }> {
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

      return { data: await response.json() as T, error: null };
    } catch (err: any) {
      const apiError = err as ApiError;
      let error: ErrorResponse;

      if (apiError.errorData) {
        error = {
          message: apiError.errorData.message || errorMessage,
          errorCode: apiError.errorData.errorCode,
          status: apiError.errorData.status,
          path: apiError.errorData.path,
          timestamp: apiError.errorData.timestamp,
          details: apiError.errorData.details
        };
      } else {
        error = {
          message: `${errorMessage}: ${apiError.message || 'Unknown error'}`
        };
      }

      return { data: null, error };
    }
  }

  /**
   * Fetches the count of cities starting with the specified letter
   *
   * @param letter - The letter to filter cities by
   * @returns A promise that resolves to the count of cities or an error
   */
  public async getCityCount(letter: string): Promise<{ count: number | null; error: ErrorResponse | null }> {
    const result = await this.fetchData<CityCount>('count', letter, 'Failed to fetch city count');
    return {
      count: result.data?.count || null,
      error: result.error
    };
  }

  /**
   * Fetches the list of cities starting with the specified letter
   *
   * @param letter - The letter to filter cities by
   * @returns A promise that resolves to the list of cities or an error
   */
  public async getCityList(letter: string): Promise<{ cities: string[] | null; error: ErrorResponse | null }> {
    const result = await this.fetchData<string[]>('list', letter, 'Failed to fetch city list');
    return {
      cities: result.data || null,
      error: result.error
    };
  }
}

// Create and export a singleton instance of the WeatherApiClient
export const weatherApiClient = new WeatherApiClient();
