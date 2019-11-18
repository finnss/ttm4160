import React from "react";
import { Link } from "react-router-dom";
import "./frontpage.css";
import Bathrooms from "./bathrooms";

function Frontpage(props) {
  return (
    <div className="frontpage">
      <div className="routeButtons">
        <Link to="/reservations">
          <button>My Reservations</button>
        </Link>
        <Link to="/favorites">
          <button>Favorites</button>
        </Link>
        <Link to="/about">
          <button>About us</button>
        </Link>
      </div>
      <Bathrooms {...props} />
    </div>
  );
}

export default Frontpage;
