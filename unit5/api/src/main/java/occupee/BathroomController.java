package occupee;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

import com.sun.org.apache.xpath.internal.operations.Bool;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class BathroomController {

    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();
    private List<Bathroom> bathrooms = new ArrayList<>();

    @RequestMapping(value = "/bathrooms", method = RequestMethod.GET)
    public Bathroom bathrooms() {
        System.out.println("Received get request");
        LinkedHashMap<String, String> location = new LinkedHashMap();
        location.put("lat", "someLat");
        location.put("long", "someLong");
        return new Bathroom("someName", 3, location);
    }

    @RequestMapping(
            value="/bathrooms", method=RequestMethod.POST, consumes="application/json", produces="application/json"
    )
    public ResponseEntity addBathroom(@RequestBody LinkedHashMap bathroomRaw) {
        System.out.println("Received post request");
        System.out.println(bathroomRaw);
        String roomName = (String) bathroomRaw.get("roomName");
        ArrayList<Boolean> availability = (ArrayList<Boolean>) bathroomRaw.get("availability");
        LinkedHashMap location = (LinkedHashMap) bathroomRaw.get("location");

        System.out.println("roomName: " + roomName);
        System.out.println("availability: " + availability);
        System.out.println("location: " + location);
        Bathroom bathroom = new Bathroom(roomName, availability, location);
        bathrooms.add(bathroom);
        System.out.print("bathroom: ");
        System.out.println(bathroom);
        return new ResponseEntity(HttpStatus.CREATED);
    }
}