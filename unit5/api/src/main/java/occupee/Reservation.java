package occupee;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class Reservation {
    private UUID id;
    private UUID bathroomId;
    // private int stall;
    private long fromTimestamp;
    private int durationInMinutes;

    public Reservation(UUID bathroomId, long fromTimestamp, int durationInMinutes) {
        this.id = UUID.randomUUID();
        this.bathroomId = bathroomId;
        this.fromTimestamp = fromTimestamp;
        this.durationInMinutes = durationInMinutes;
    }

    public Reservation(UUID bathroomId, long fromTimestamp) {
        this.id = UUID.randomUUID();
        this.bathroomId = bathroomId;
        this.fromTimestamp = fromTimestamp;
        this.durationInMinutes = 5;
    }

    public UUID getId() {
        return id;
    }

    public UUID getBathroomId() {
        return bathroomId;
    }

    public void setBathroomId(UUID bathroomId) {
        this.bathroomId = bathroomId;
    }

    public long getFromTimestamp() {
        return fromTimestamp;
    }

    public void setFromTimestamp(long fromTimestamp) {
        this.fromTimestamp = fromTimestamp;
    }

    public int getDurationInMinutes() {
        return durationInMinutes;
    }

    public void setDurationInMinutes(int durationInMinutes) {
        this.durationInMinutes = durationInMinutes;
    }
}
