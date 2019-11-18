import React, { useState } from "react";
import "./reserve.css";
import moment from "moment";
import TimePicker from "react-time-picker";

function Reserve({ bathrooms }: props) {
  console.log("hello world", moment().format("hh:ss"));
  const [startTime, setStartTime] = useState(moment().format("hh:ss"));

  return (
    <div className="reserve">
      <h2>Reserve bathroom</h2>
      <form>
        <div className="inputRow">
          <label>Bathroom:</label>
          <select>
            {bathrooms.map(bathroom => (
              <option value={bathroom.id}>{bathroom.roomName}</option>
            ))}
          </select>
        </div>

        <div className="inputRow time">
          <label>From:</label>
          <TimePicker
            value={startTime}
            onChange={value => setStartTime(value)}
          />
        </div>

        <div className="inputRow">
          <label>Duration (minutes):</label>
          <input
            type="number"
            defaultValue={5}
            name="durationInMinutes"
            style={{ width: "25px" }}
          />
        </div>
        <button type="submit" style={{ marginTop: "20px" }}>
          Reserve
        </button>
      </form>
    </div>
  );
}

export default Reserve;
