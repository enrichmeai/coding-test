document.addEventListener('DOMContentLoaded', function() {
    const letterInput = document.getElementById('letterInput');
    const searchButton = document.getElementById('searchButton');
    const clearButton = document.getElementById('clearButton');
    const validationMessage = document.getElementById('validationMessage');
    const loading = document.getElementById('loading');
    const result = document.getElementById('result');
    const cityList = document.getElementById('cityList');
    const searchedLetter = document.getElementById('searchedLetter');
    const cityCount = document.getElementById('cityCount');
    const cities = document.getElementById('cities');
    const error = document.getElementById('error');
    const errorMessage = document.getElementById('errorMessage');

    // Add event listener to the search button
    searchButton.addEventListener('click', searchCities);

    // Add event listener to the clear button
    clearButton.addEventListener('click', clearForm);

    // Add event listener to the input field for Enter key
    letterInput.addEventListener('keypress', function(e) {
        if (e.key === 'Enter') {
            searchCities();
        }
    });

    // Add event listener to the input field for validation
    letterInput.addEventListener('input', validateInput);

    // Function to search cities
    function searchCities() {
        const letter = letterInput.value.trim();

        // Validate input
        if (!letter) {
            showError('Please enter a letter');
            return;
        }

        if (!isLetter(letter)) {
            showError('Please enter a valid letter (A-Z or a-z)');
            return;
        }

        // Reset UI
        resetUI();

        // Show loading indicator
        loading.classList.remove('hidden');

        // Fetch count of cities
        fetchCityCount(letter);

        // Fetch list of cities
        fetchCityList(letter);
    }

    // Function to fetch count of cities
    function fetchCityCount(letter) {
        fetch(`/api/weather/cities/count?letter=${letter}`)
            .then(response => {
                if (!response.ok) {
                    // Try to parse error response
                    return response.json().then(errorData => {
                        throw { message: `HTTP error! Status: ${response.status}`, errorData };
                    }).catch(parseError => {
                        // If parsing fails, throw original error
                        throw new Error(`HTTP error! Status: ${response.status}`);
                    });
                }
                return response.json();
            })
            .then(data => {
                // Update UI with count
                searchedLetter.textContent = letter;
                cityCount.textContent = data.count;
                result.classList.remove('hidden');
            })
            .catch(err => {
                if (err.errorData) {
                    // If we have error data from the server
                    showError(`Error: ${err.errorData.message || 'Failed to fetch city count'}`, err.errorData);
                } else {
                    // If we don't have error data
                    showError(`Failed to fetch city count: ${err.message}`);
                }
            })
            .finally(() => {
                loading.classList.add('hidden');
            });
    }

    // Function to fetch list of cities
    function fetchCityList(letter) {
        fetch(`/api/weather/cities?letter=${letter}`)
            .then(response => {
                if (!response.ok) {
                    // Try to parse error response
                    return response.json().then(errorData => {
                        throw { message: `HTTP error! Status: ${response.status}`, errorData };
                    }).catch(parseError => {
                        // If parsing fails, throw original error
                        throw new Error(`HTTP error! Status: ${response.status}`);
                    });
                }
                return response.json();
            })
            .then(data => {
                // Clear previous list
                cities.innerHTML = '';

                // Add cities to list
                if (data.length > 0) {
                    data.forEach(city => {
                        const li = document.createElement('li');
                        li.textContent = city;
                        cities.appendChild(li);
                    });
                    cityList.classList.remove('hidden');
                }
            })
            .catch(err => {
                if (err.errorData) {
                    // If we have error data from the server
                    showError(`Error: ${err.errorData.message || 'Failed to fetch city list'}`, err.errorData);
                } else {
                    // If we don't have error data
                    showError(`Failed to fetch city list: ${err.message}`);
                }
            });
    }

    // Function to show error
    function showError(message, errorData) {
        // Set error message
        errorMessage.textContent = message;

        // If we have detailed error data from the server
        if (errorData) {
            // Set error code
            document.getElementById('errorCode').textContent = errorData.errorCode || 'N/A';

            // Set error status
            document.getElementById('errorStatus').textContent = errorData.status || 'N/A';

            // Set error path
            document.getElementById('errorPath').textContent = errorData.path || 'N/A';

            // Set error time
            document.getElementById('errorTime').textContent =
                errorData.timestamp ? new Date(errorData.timestamp).toLocaleString() : 'N/A';

            // Set error details if available
            if (errorData.details) {
                document.getElementById('errorDetailsText').textContent = errorData.details;
                document.getElementById('errorDetails').classList.remove('hidden');
            } else {
                document.getElementById('errorDetails').classList.add('hidden');
            }
        } else {
            // If no detailed error data, set default values
            document.getElementById('errorCode').textContent = 'N/A';
            document.getElementById('errorStatus').textContent = 'N/A';
            document.getElementById('errorPath').textContent = 'N/A';
            document.getElementById('errorTime').textContent = 'N/A';
            document.getElementById('errorDetails').classList.add('hidden');
        }

        // Show error container and hide loading
        error.classList.remove('hidden');
        loading.classList.add('hidden');
    }

    // Function to reset UI
    function resetUI() {
        result.classList.add('hidden');
        cityList.classList.add('hidden');
        error.classList.add('hidden');
    }

    // Function to check if input is a letter
    function isLetter(str) {
        return str.length === 1 && str.match(/[a-z]/i) !== null;
    }

    // Function to validate input
    function validateInput() {
        const letter = letterInput.value.trim();

        if (!letter) {
            validationMessage.textContent = '';
            validationMessage.classList.add('hidden');
            return;
        }

        if (!isLetter(letter)) {
            validationMessage.textContent = 'Please enter a valid letter (A-Z or a-z)';
            validationMessage.classList.remove('hidden');
            validationMessage.classList.add('error');
            validationMessage.classList.remove('success');
        } else {
            validationMessage.textContent = 'Valid input';
            validationMessage.classList.remove('hidden');
            validationMessage.classList.add('success');
            validationMessage.classList.remove('error');
        }
    }

    // Function to clear the form
    function clearForm() {
        letterInput.value = '';
        resetUI();
        validationMessage.textContent = '';
        validationMessage.classList.add('hidden');
    }
});
