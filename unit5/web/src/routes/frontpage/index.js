import React from "react";
import { Link } from "react-router-dom";
import "./frontpage.css";
import Bathrooms from "./bathrooms";

function Frontpage(props) {
  return (
    <div className="frontpage">
      <div className="routeButtons">
        <Link to="/reserve">
          <button>Reserve</button>
        </Link>
        <Link to="/confirmation">
          <button>My reservations</button>
        </Link>
        <Link to="/about">
          <button>About</button>
        </Link>
      </div>
      <Bathrooms {...props} />
    </div>
  );
}

export default Frontpage;
