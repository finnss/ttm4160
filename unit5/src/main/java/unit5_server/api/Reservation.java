package main.java.unit5_server.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class Reservation {
    private UUID id;
    private UUID bathroomId;
    private int stall;
    private long fromTimestamp;
    private int durationInMinutes;

    public Reservation(UUID bathroomId, int stall, long fromTimestamp, int durationInMinutes) {
        this.id = UUID.randomUUID();
        this.bathroomId = bathroomId;
        this.stall = stall;
        this.fromTimestamp = fromTimestamp;
        this.durationInMinutes = durationInMinutes;
    }

    public UUID getBathroomId() {
        return bathroomId;
    }

    public void setBathroomId(UUID bathroomId) {
        this.bathroomId = bathroomId;
    }

    public int getStall() {
        return stall;
    }

    public void setStall(int stall) {
        this.stall = stall;
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
