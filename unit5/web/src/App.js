import React, { useEffect, useState } from "react";
import "./App.css";
import Routes from "./routes/";
import { hot } from "react-hot-loader/root";

function App() {
  const [bathrooms, setBathrooms] = useState([]);
  const [reservations, setReservations] = useState([]);
  const [myReservations, setMyReservations] = useState([]);

  useEffect(() => {
    const url = "http://localhost:8080";
    if (bathrooms.length === 0) {
      fetch(`${url}/bathrooms`, {
        method: "GET",
        headers: {
          "Content-Type": "application/json"
        }
      })
        .then(result => result.json())
        .then(result => setBathrooms([...bathrooms, ...result]));
      fetch(`${url}/reservations`)
        .then(result => result.json())
        .then(result => setReservations([...reservations, ...result]));
    }
  });
  return (
    <div className="App">
      <header className="App-header">
        <Routes
          bathrooms={bathrooms}
          reservations={reservations}
          setBathrooms={setBathrooms}
          setReservations={setReservations}
          myReservations={myReservations}
          setMyReservations={setMyReservations}
          addReservationToState={newReg =>
            setMyReservations([...myReservations, newReg])
          }
        />
      </header>
    </div>
  );
}

export default hot(App);
