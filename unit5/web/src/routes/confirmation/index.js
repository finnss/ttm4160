import React from "react";
import { Link } from "react-router-dom";

function Confirmation() {
  return (
    <div className="confirmation">
      <h2>Reservation confirmed</h2>
      <div style={{ marginBottom: "15px" }}>
        Thank you! Your reservation has been confirmed.
      </div>

      <Link to="/">Back to the frontpage</Link>
    </div>
  );
}

export default Confirmation;
