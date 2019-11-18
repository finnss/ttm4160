import React from "react";
import { Link } from "react-router-dom";
import moment from "moment";

function Confirmation({ bathrooms, myReservations }: props) {
  console.log("myRegistrations", myReservations);
  console.log("bathrooms", bathrooms);
  return (
    <div className="confirmation">
      <h2>Reservation confirmed</h2>
      <div style={{ marginBottom: "15px" }}>
        Thank you! Your reservation has been confirmed.
      </div>

      {myReservations && myReservations.length > 0 && (
        <div>
          <div>Your reservations:</div>
          {myReservations.map(reg => (
            <div style={{ marginBottom: "15px" }}>
              <div>
                {
                  bathrooms.find(bathroom => bathroom.id === reg.bathroomId)
                    .roomName
                }
              </div>
              <div>{moment(reg.fromTimestamp).format("hh:mm, MMMM Do")}</div>
            </div>
          ))}
        </div>
      )}
      <Link to="/">Back to the frontpage</Link>
    </div>
  );
}

export default Confirmation;
