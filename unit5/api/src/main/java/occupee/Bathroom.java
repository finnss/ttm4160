package occupee;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

public class Bathroom {

    private UUID id;
    private String roomName;
    private List<Boolean> availability;
    private LinkedHashMap<String, String> location;

    public Bathroom(String roomName, List availability, LinkedHashMap<String, String> location) {
        this.roomName = roomName;
        this.availability = availability;
        this.location = location;
        this.id = UUID.randomUUID();
    }

    public Bathroom(String roomName, int numberOfStalls, LinkedHashMap<String, String> location) {
        this(roomName, null, location);

        List<Boolean> availability = new ArrayList<>();
        for (int i = 0; i < numberOfStalls; i++) {
            availability.add(false);
        }
        this.availability = availability;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public List<Boolean> getAvailability() {
        return availability;
    }

    public void setAvailability(List<Boolean> availability) {
        this.availability = availability;
    }

    public LinkedHashMap<String, String> getLocation() {
        return location;
    }

    public void setLocation(LinkedHashMap<String, String> location) {
        this.location = location;
    }

    public String toString() {
        JSONObject json = new JSONObject();
        json.put("roomName", roomName);
        json.put("availability", availability);
        json.put("location", location);
        return json.toString();
    }
}
