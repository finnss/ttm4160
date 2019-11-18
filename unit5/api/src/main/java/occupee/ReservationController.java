package occupee;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

@RestController
public class ReservationController {

    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();
    private List<Reservation> reservations = new ArrayList<>();

    @CrossOrigin(origins = "*")
    @RequestMapping(value = "/reservations", method = RequestMethod.GET)
    public ResponseEntity reservations() {
        System.out.println("Received get request for reservations");
        if (reservations.size() == 0) {
            System.out.println("There were no reservations to show; generating a template one.");
            UUID id = UUID.fromString("c3dc3c06-a9c7-4d21-b2c0-76085d7f51d3");
            reservations.add(new Reservation(id, System.currentTimeMillis()));
        }
        return new ResponseEntity(reservations, HttpStatus.OK);

    }

    @CrossOrigin(origins = "*")
    @RequestMapping(
            value="/reservations", method=RequestMethod.POST, consumes="application/json", produces="application/json"
    )
    public ResponseEntity makeReservation(@RequestBody LinkedHashMap reservationRaw) {
        System.out.println("Received post request for reservations");
        UUID bathroomId = UUID.fromString((String) reservationRaw.get("bathroomId"));
        long fromTimestamp = (long) reservationRaw.get("fromTimestamp");
        Integer durationInMinutes = (Integer) reservationRaw.get("durationInMinutes");
        if (durationInMinutes == null) {
            durationInMinutes = 5;
        }

        Reservation reservation = new Reservation(bathroomId, fromTimestamp, durationInMinutes);
        reservations.add(reservation);

        return new ResponseEntity(HttpStatus.CREATED);
    }
}