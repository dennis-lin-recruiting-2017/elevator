package com.dennis.interviews.elevators;

/**
 * Models a person who rides an elevator.  Meant to capture statistics to perform an queuing theory analysis.
 *
 * @author dennislin
 *
 */
public class PickupRequest {
    private final int startingFloor;
    private final int targetFloor;
    private final int direction;
    private final double timestamp;

    private double timestampPickup = 0.0;
    private double timestampDropoff = 0.0;

    @SuppressWarnings("unused")
    private PickupRequest() {
        throw new RuntimeException("Should not call default constructor.");
    }

    public PickupRequest(final int startingFloor, final int direction, double timestamp) {
        this.startingFloor = startingFloor;
        this.direction = direction;
        this.targetFloor = startingFloor + direction;
        this.timestamp = timestamp;
    }

    public int getStartingFloor() {
        return startingFloor;
    }

    public int getTargetFloor() {
        return targetFloor;
    }

    public void pickup(final double timestamp) {
        timestampPickup = timestamp;
    }

    public void dropOff(final double timestamp) {
        timestampDropoff = timestamp;
    }

    public double getTimestamp() {
        return timestamp;
    }

    public void setTimestampPickup(final double timestamp) {
        timestampPickup = timestamp;
    }

    public double getTimestampPickup() {
        return timestampPickup;
    }

    public void setTimestampDropoff(final double timestamp) {
        timestampDropoff = timestamp;
    }

    public double getTimestampDropoff() {
        return timestampDropoff;
    }

    @Override
    public String toString() {
        return String.format("PickupRequest(startFloor=%2d, direction=%2d, targetFloor=%2d, timestamp=%f",
                startingFloor, direction, targetFloor, timestamp);
    }
}
