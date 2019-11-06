import React from "react";
import logo from "./logo.svg";
import "./App.css";
import Routes from "./routes/";
import { hot } from "react-hot-loader/root";

function App() {
  return (
    <div className="App">
      <header className="App-header">
        <Routes />
      </header>
    </div>
  );
}

export default hot(App);
