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
      {bathrooms.map(bathroom => {
        const isFullyOccupied =
          bathroom.numberOfOccupiedStalls + bathroom.numberOfReservedStalls >=
          bathroom.numberOfStalls;
        console.log("occ", bathroom, isFullyOccupied);
        return (
          <div className="tableRow bathroom">
            <div>{bathroom.roomName}</div>
            <div style={{ color: isFullyOccupied ? "red" : "green" }}>
              {isFullyOccupied ? "Full" : "Free"}
            </div>
            <div>30m</div>
            <Link to="/reserve">Reserve ></Link>
          </div>
        );
      })}
    </div>
  );
}

export default Bathrooms;
