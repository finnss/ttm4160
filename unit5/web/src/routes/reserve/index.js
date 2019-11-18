import React, { useState, useEffect } from "react";
import "./reserve.css";
import DatePicker from "react-datepicker";
import { withRouter } from "react-router-dom";

function Reserve({
  bathrooms,
  reservations,
  addReservationToState,
  history
}: props) {
  const [bathroomId, setBathroomId] = useState(
    bathrooms && bathrooms[0] && bathrooms[0].id
  );
  const [startTime, setStartTime] = useState(new Date());
  const [durationInMinutes, setDuration] = useState(5);
  const [errorMessage, setErrorMessage] = useState(undefined);

  const validateCurrentState = () => {
    let isValid = true;
    const currentStateEndTime =
      startTime.getTime() + durationInMinutes * 60 * 1000;
    reservations.forEach(reservation => {
      const endTime =
        reservation.fromTimestamp + reservation.durationInMinutes * 60 * 1000;
      console.log("bathroom", reservation, "endTime", endTime);
      console.log(
        "startTime",
        startTime,
        "currentStateEndTime",
        currentStateEndTime
      );
      if (
        (endTime >= startTime && endTime <= currentStateEndTime) ||
        (endTime >= currentStateEndTime &&
          reservation.fromTimestamp < currentStateEndTime)
      ) {
        console.log("ERROR");
        isValid = false;
      }
    });
    console.log(isValid);
    return isValid;
  };

  const handleSubmit = event => {
    event.preventDefault();
    setErrorMessage("");

    const isValid = validateCurrentState();
    if (!isValid) {
      setErrorMessage("That time slot is already taken!");
      return;
    }

    const url = "http://localhost:8080";

    const fromTimestamp = startTime.getTime();
    console.log("fromTimestamp", fromTimestamp);

    const payload = {
      bathroomId,
      fromTimestamp,
      durationInMinutes
    };
    console.log("in handlesubmit. payload:", payload);

    fetch(`${url}/reservations`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify(payload)
    })
      .then(response => {
        console.log("reponse!", response);
        addReservationToState(payload);
      })
      .then(() => history.push("/confirmation"));
  };

  useEffect(() => {
    if (bathrooms[0] && !bathroomId) {
      setBathroomId(bathrooms[0].id);
    }
  });

  return (
    <div className="reserve">
      <h2>Reserve bathroom</h2>
      <form onSubmit={handleSubmit}>
        <div className="inputRow">
          <label>Bathroom:</label>
          <select>
            {bathrooms.map(bathroom => (
              <option
                value={bathroomId}
                onChange={e => setBathroomId(e.target.value)}
              >
                {bathroom.roomName}
              </option>
            ))}
          </select>
        </div>

        <div className="inputRow time">
          <label>From:</label>
          <DatePicker
            selected={startTime}
            onChange={date => setStartTime(date)}
            showTimeSelect
            timeFormat="HH:mm"
            timeIntervals={15}
            timeCaption="time"
            dateFormat="hh:mm, MMMM d"
          />
        </div>

        <div className="inputRow">
          <label>Duration (minutes):</label>
          <input
            type="number"
            value={durationInMinutes}
            name="durationInMinutes"
            style={{ width: "25px" }}
            onChange={e => setDuration(e.target.value)}
          />
        </div>
        <button type="submit" style={{ marginTop: "20px" }}>
          Reserve
        </button>
      </form>
      {errorMessage && <div className="error">{errorMessage}</div>}
    </div>
  );
}

export default withRouter(Reserve);
