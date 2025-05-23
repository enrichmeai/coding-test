import React from 'react';
import './App.css';
import CityLetterFinder from './components/CityLetterFinder';

// Define the component's props interface (empty in this case)
interface AppProps {}

class App extends React.Component<AppProps> {
  render() {
    return (
      <div className="App">
        <header className="App-header">
          <h1>City Letter Finder</h1>
        </header>
        <main>
          <CityLetterFinder />
        </main>
      </div>
    );
  }
}

export default App;
