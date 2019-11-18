import React from "react";
import moment from "moment";

function Reservations({ bathrooms, myReservations }: props) {
  return (
    <div className="reservations">
      <h2>Reservations</h2>

      {myReservations && myReservations.length > 0 ? (
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
      ) : (
        <div>You have no reservation yet!</div>
      )}
    </div>
  );
}

export default Reservations;
