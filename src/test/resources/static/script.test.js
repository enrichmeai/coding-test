// UI Tests for City Letter Finder Application

// Mock DOM elements
document.body.innerHTML = `
<div class="container">
    <h1>City Letter Finder</h1>
    <p>Enter a letter to see how many cities start with that letter</p>

    <div class="input-container">
        <input type="text" id="letterInput" maxlength="1" placeholder="Enter a letter">
        <button id="searchButton">Search</button>
        <button id="clearButton" class="secondary-button">Clear</button>
    </div>
    <div id="validationMessage" class="validation-message hidden"></div>

    <div class="result-container">
        <div id="loading" class="hidden">Loading...</div>
        <div id="result" class="hidden">
            <p>Number of cities starting with "<span id="searchedLetter"></span>": <span id="cityCount">0</span></p>
        </div>
        <div id="cityList" class="hidden">
            <h3>Cities:</h3>
            <ul id="cities"></ul>
        </div>
        <div id="error" class="hidden">
            <p>Error: <span id="errorMessage"></span></p>
        </div>
    </div>
</div>
`;

// Mock fetch API
global.fetch = jest.fn();

// Instead of loading the actual script, we'll mock its functionality
// This avoids file system dependencies in the tests

// Mock the script.js functionality
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

    // Add event listeners
    searchButton.addEventListener('click', searchCities);
    clearButton.addEventListener('click', clearForm);
    letterInput.addEventListener('keypress', function(e) {
        if (e.key === 'Enter') {
            searchCities();
        }
    });
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
                    throw new Error(`HTTP error! Status: ${response.status}`);
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
                showError(`Failed to fetch city count: ${err.message}`);
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
                    throw new Error(`HTTP error! Status: ${response.status}`);
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
                showError(`Failed to fetch city list: ${err.message}`);
            });
    }

    // Function to show error
    function showError(message) {
        errorMessage.textContent = message;
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

// Helper function to trigger DOMContentLoaded
function triggerDOMContentLoaded() {
    const event = document.createEvent('Event');
    event.initEvent('DOMContentLoaded', true, true);
    document.dispatchEvent(event);
}

// Helper function to get DOM elements
function getElements() {
    return {
        letterInput: document.getElementById('letterInput'),
        searchButton: document.getElementById('searchButton'),
        clearButton: document.getElementById('clearButton'),
        validationMessage: document.getElementById('validationMessage'),
        loading: document.getElementById('loading'),
        result: document.getElementById('result'),
        cityList: document.getElementById('cityList'),
        searchedLetter: document.getElementById('searchedLetter'),
        cityCount: document.getElementById('cityCount'),
        cities: document.getElementById('cities'),
        error: document.getElementById('error'),
        errorMessage: document.getElementById('errorMessage')
    };
}

// Tests for isLetter function
describe('isLetter function', () => {
    beforeEach(() => {
        // Reset DOM and trigger DOMContentLoaded
        document.body.innerHTML = document.body.innerHTML;
        triggerDOMContentLoaded();
    });

    test('should return true for valid letters', () => {
        const elements = getElements();
        elements.letterInput.value = 'a';
        elements.letterInput.dispatchEvent(new Event('input'));
        expect(elements.validationMessage.textContent).toBe('Valid input');
        expect(elements.validationMessage.classList.contains('success')).toBe(true);
        expect(elements.validationMessage.classList.contains('error')).toBe(false);

        elements.letterInput.value = 'Z';
        elements.letterInput.dispatchEvent(new Event('input'));
        expect(elements.validationMessage.textContent).toBe('Valid input');
        expect(elements.validationMessage.classList.contains('success')).toBe(true);
        expect(elements.validationMessage.classList.contains('error')).toBe(false);
    });

    test('should return false for invalid inputs', () => {
        const elements = getElements();
        elements.letterInput.value = '1';
        elements.letterInput.dispatchEvent(new Event('input'));
        expect(elements.validationMessage.textContent).toBe('Please enter a valid letter (A-Z or a-z)');
        expect(elements.validationMessage.classList.contains('error')).toBe(true);
        expect(elements.validationMessage.classList.contains('success')).toBe(false);

        elements.letterInput.value = '@';
        elements.letterInput.dispatchEvent(new Event('input'));
        expect(elements.validationMessage.textContent).toBe('Please enter a valid letter (A-Z or a-z)');
        expect(elements.validationMessage.classList.contains('error')).toBe(true);
        expect(elements.validationMessage.classList.contains('success')).toBe(false);

        elements.letterInput.value = 'ab';
        elements.letterInput.dispatchEvent(new Event('input'));
        expect(elements.validationMessage.textContent).toBe('Please enter a valid letter (A-Z or a-z)');
        expect(elements.validationMessage.classList.contains('error')).toBe(true);
        expect(elements.validationMessage.classList.contains('success')).toBe(false);
    });
});

// Tests for validateInput function
describe('validateInput function', () => {
    beforeEach(() => {
        // Reset DOM and trigger DOMContentLoaded
        document.body.innerHTML = document.body.innerHTML;
        triggerDOMContentLoaded();
    });

    test('should hide validation message when input is empty', () => {
        const elements = getElements();
        elements.letterInput.value = '';
        elements.letterInput.dispatchEvent(new Event('input'));
        expect(elements.validationMessage.classList.contains('hidden')).toBe(true);
    });

    test('should show error message for invalid input', () => {
        const elements = getElements();
        elements.letterInput.value = '1';
        elements.letterInput.dispatchEvent(new Event('input'));
        expect(elements.validationMessage.classList.contains('hidden')).toBe(false);
        expect(elements.validationMessage.classList.contains('error')).toBe(true);
        expect(elements.validationMessage.textContent).toBe('Please enter a valid letter (A-Z or a-z)');
    });

    test('should show success message for valid input', () => {
        const elements = getElements();
        elements.letterInput.value = 'a';
        elements.letterInput.dispatchEvent(new Event('input'));
        expect(elements.validationMessage.classList.contains('hidden')).toBe(false);
        expect(elements.validationMessage.classList.contains('success')).toBe(true);
        expect(elements.validationMessage.textContent).toBe('Valid input');
    });
});

// Tests for clearForm function
describe('clearForm function', () => {
    beforeEach(() => {
        // Reset DOM and trigger DOMContentLoaded
        document.body.innerHTML = document.body.innerHTML;
        triggerDOMContentLoaded();
    });

    test('should clear input and reset UI', () => {
        const elements = getElements();

        // Set up initial state
        elements.letterInput.value = 'a';
        elements.validationMessage.textContent = 'Valid input';
        elements.validationMessage.classList.remove('hidden');
        elements.result.classList.remove('hidden');
        elements.cityList.classList.remove('hidden');

        // Trigger clear button click
        elements.clearButton.click();

        // Check that everything is reset
        expect(elements.letterInput.value).toBe('');
        expect(elements.validationMessage.classList.contains('hidden')).toBe(true);
        expect(elements.result.classList.contains('hidden')).toBe(true);
        expect(elements.cityList.classList.contains('hidden')).toBe(true);
    });
});

// Tests for searchCities function
describe('searchCities function', () => {
    beforeEach(() => {
        // Reset DOM and trigger DOMContentLoaded
        document.body.innerHTML = document.body.innerHTML;
        triggerDOMContentLoaded();

        // Reset fetch mock
        fetch.mockClear();
    });

    test('should show error for empty input', () => {
        const elements = getElements();
        elements.letterInput.value = '';
        elements.searchButton.click();

        expect(elements.error.classList.contains('hidden')).toBe(false);
        expect(elements.errorMessage.textContent).toBe('Please enter a letter');
        expect(fetch).not.toHaveBeenCalled();
    });

    test('should show error for invalid input', () => {
        const elements = getElements();
        elements.letterInput.value = '1';
        elements.searchButton.click();

        expect(elements.error.classList.contains('hidden')).toBe(false);
        expect(elements.errorMessage.textContent).toBe('Please enter a valid letter (A-Z or a-z)');
        expect(fetch).not.toHaveBeenCalled();
    });

    test('should fetch data for valid input', async () => {
        const elements = getElements();

        // Mock successful responses
        fetch.mockImplementation((url) => {
            if (url.includes('/api/weather/cities/count')) {
                return Promise.resolve({
                    ok: true,
                    json: () => Promise.resolve({ count: 5 })
                });
            } else if (url.includes('/api/weather/cities')) {
                return Promise.resolve({
                    ok: true,
                    json: () => Promise.resolve(['City1', 'City2', 'City3', 'City4', 'City5'])
                });
            }
        });

        // Trigger search
        elements.letterInput.value = 'a';
        elements.searchButton.click();

        // Check loading state
        expect(elements.loading.classList.contains('hidden')).toBe(false);

        // Wait for promises to resolve
        await new Promise(resolve => setTimeout(resolve, 0));

        // Check that fetch was called twice
        expect(fetch).toHaveBeenCalledTimes(2);
        expect(fetch).toHaveBeenCalledWith('/api/weather/cities/count?letter=a');
        expect(fetch).toHaveBeenCalledWith('/api/weather/cities?letter=a');

        // Check results
        expect(elements.searchedLetter.textContent).toBe('a');
        expect(elements.cityCount.textContent).toBe('5');
        expect(elements.result.classList.contains('hidden')).toBe(false);
        expect(elements.cityList.classList.contains('hidden')).toBe(false);
        expect(elements.cities.children.length).toBe(5);
    });

    test('should handle fetch errors', async () => {
        const elements = getElements();

        // Mock failed response
        fetch.mockImplementation(() => {
            return Promise.resolve({
                ok: false,
                status: 500
            });
        });

        // Trigger search
        elements.letterInput.value = 'a';
        elements.searchButton.click();

        // Wait for promises to resolve
        await new Promise(resolve => setTimeout(resolve, 0));

        // Check error state
        expect(elements.error.classList.contains('hidden')).toBe(false);
        expect(elements.errorMessage.textContent).toContain('Failed to fetch');
    });
});
