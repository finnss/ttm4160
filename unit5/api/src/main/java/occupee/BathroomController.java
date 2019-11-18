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

    @CrossOrigin(origins = "*")
    @RequestMapping(value = "/bathrooms", method = RequestMethod.GET, produces="application/json")
    public ResponseEntity bathrooms() {
        System.out.println("Received get request for bathrooms");
        if (bathrooms.size() == 0) {
            System.out.println("There were no Bathrooms to show; generating a template one.");
            LinkedHashMap<String, String> location = new LinkedHashMap();
            location.put("lat", "someLat");
            location.put("long", "someLong");
            bathrooms.add(new Bathroom("someName", 3, location));
        }
        return new ResponseEntity(bathrooms, HttpStatus.OK);
    }

    @CrossOrigin(origins = "*")
    @RequestMapping(
            value="/bathrooms", method=RequestMethod.POST, consumes="application/json", produces="application/json"
    )
    public ResponseEntity addBathroom(@RequestBody LinkedHashMap bathroomRaw) {
        String roomName = (String) bathroomRaw.get("roomName");
        int numberOfStalls = (int) bathroomRaw.get("numberOfStalls");
        LinkedHashMap location = (LinkedHashMap) bathroomRaw.get("location");

        Bathroom bathroom = new Bathroom(roomName, numberOfStalls, location);
        bathrooms.add(bathroom);
        return new ResponseEntity(HttpStatus.CREATED);
    }
}