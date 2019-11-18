import React from "react";
import { Link } from "react-router-dom";
import "./bathrooms.css";

function Bathrooms({ bathrooms }: props) {
  const headerRow = bathrooms && bathrooms.length > 0 && (
    <div className="tableRow headerRow">
      <div>Name</div>
      <div>Occupied</div>
      <div>Distance</div>
      <div>Book</div>
    </div>
  );

  return (
    <div className="bathrooms">
      {headerRow}
      {bathrooms
        .map(bathroom => ({
          ...bathroom,
          distance: Math.floor(Math.random() * 100)
        }))
        .sort((a, b) => a.distance - b.distance)
        .map(bathroom => {
          const isFullyOccupied =
            bathroom.numberOfOccupiedStalls + bathroom.numberOfReservedStalls >=
            bathroom.numberOfStalls;

          return (
            <div className="tableRow bathroom">
              <div>{bathroom.roomName}</div>
              <div style={{ color: isFullyOccupied ? "red" : "green" }}>
                {isFullyOccupied ? "Full" : "Free"}
              </div>
              <div>{bathroom.distance}m</div>
              <Link to="/reserve">Reserve ></Link>
            </div>
          );
        })}
    </div>
  );
}

export default Bathrooms;
