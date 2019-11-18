import React from "react";
import { Link } from "react-router-dom";
import "./frontpage.css";

function Frontpage() {
  return (
    <div className="frontpage">
      <div className="routeButtons">
        <Link to="/reserve">
          <button>Reserve</button>
        </Link>
        <Link to="/about">
          <button>About</button>
        </Link>
      </div>
    </div>
  );
}

export default Frontpage;
