import React from "react";
import { Link } from "react-router-dom";
import "./frontpage.css";

function Frontpage() {
  return (
    <div className="frontpage">
      <h1 className="title">Occupee</h1>
      <div className="routeButtons">
        <Link to="/book">
          <button>Book</button>
        </Link>
        <Link to="/about">
          <button>About</button>
        </Link>
      </div>
    </div>
  );
}

export default Frontpage;
