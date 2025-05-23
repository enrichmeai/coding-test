import React from 'react';
import './App.css';
import CityLetterFinder from './components/CityLetterFinder';

const App: React.FC = () => {
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
};

export default App;
