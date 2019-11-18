package occupee;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

public class Bathroom {

    private UUID id;
    private String roomName;
    private int numberOfStalls;
    private int numberOfOccupiedStalls = 0;
    private int numberOfReservedStalls = 0;
    private LinkedHashMap<String, String> location;

    public Bathroom(String roomName, int numberOfStalls, LinkedHashMap<String, String> location) {
        this.roomName = roomName;
        this.location = location;
        this.numberOfStalls = numberOfStalls;
        this.id = UUID.randomUUID();
    }

    public UUID getId() {
        return id;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public int getNumberOfStalls() {
        return numberOfStalls;
    }

    public void setNumberOfStalls(int numberOfStalls) {
        this.numberOfStalls = numberOfStalls;
    }

    public int getNumberOfOccupiedStalls() {
        return numberOfOccupiedStalls;
    }

    public void setNumberOfOccupiedStalls(int numberOfOccupiedStalls) {
        this.numberOfOccupiedStalls = numberOfOccupiedStalls;
    }

    public int getNumberOfReservedStalls() {
        return numberOfReservedStalls;
    }

    public void setNumberOfReservedStalls(int numberOfReservedStalls) {
        this.numberOfReservedStalls = numberOfReservedStalls;
    }

    public LinkedHashMap<String, String> getLocation() {
        return location;
    }

    public void setLocation(LinkedHashMap<String, String> location) {
        this.location = location;
    }

}
