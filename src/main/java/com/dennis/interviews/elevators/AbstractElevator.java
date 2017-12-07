package com.dennis.interviews.elevators;

import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The base representation of an elevator.
 *
 * @author dennislin
 */
public abstract class AbstractElevator {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractElevator.class);
    private static final double DELTA_ALLOWED = 0.0001;

    public static enum State {
        ASCENDING,
        DESCENDING,
        LOADING,
        IDLE;
    };

    private final String name;

    private double minFloor = 0.0;
    private double maxFloor = 10.0;
    private double targetFloor = 0.0;
    private double pickupTimeRequired = 15.0;
    private double speed = 0.10;
    private double currentPosition = 0.0;
    private double timeInCurrentState = 0.0;
    private double currentTimestamp = 0.0;
    private State state = State.IDLE;
    private int maxWeight = 15;
    private int maxArea = 10;
    private List<PickupRequest> listActiveRequests = new LinkedList<>();
    private List<PickupRequest> listServicedRequests = new LinkedList<>();
    private Simulation simulation = null;

    private AbstractElevator() {
        throw new RuntimeException("Should not use default constructor");
    }

    public AbstractElevator(final String name) {
        this.name = name;
    }

    /**
     * Sets the simulation that this elevator is associated with.
     *
     * NOTE:  Package visibility so only the abstract objects can set this value.
     *
     * @param newSimulation the simulation that this elevator is associated with
     */
    void setSimulation(Simulation newSimulation) {
        simulation = newSimulation;
    }

    protected Simulation getSimulation() {
        return simulation;
    }

    public double getMinFloor() {
        return minFloor;
    }

    public void setMinFloor(double minFloor) {
        this.minFloor = minFloor;
    }

    public double getMaxFloor() {
        return maxFloor;
    }

    public void setMaxFloor(double maxFloor) {
        this.maxFloor = maxFloor;
    }

    public void setTargetFloor(double newTargetFloor) {
        if (newTargetFloor < minFloor) {
            throw new IllegalArgumentException(
                    String.format("Attempted to target floor of Elevator(%s) to %f.  Min floor is $f",
                            name, newTargetFloor, minFloor));
        } else if (newTargetFloor > maxFloor) {
            throw new IllegalArgumentException(
                    String.format("Attempted to target floor of Elevator(%s) to %f.  Max floor is $f",
                            name, newTargetFloor, maxFloor));
        }

        if (State.IDLE == state) {
            if (newTargetFloor > currentPosition) {
                state = State.ASCENDING;
            } else {
                state = State.DESCENDING;
            }

            targetFloor = newTargetFloor;
        } else if (State.ASCENDING == state) {
            if (newTargetFloor > targetFloor) {
                targetFloor = newTargetFloor;
            }
        } else if (State.DESCENDING == state) {
            if (newTargetFloor < targetFloor) {
                targetFloor = newTargetFloor;
            }
        }
    }

    public static final boolean didCrossFloorAscending(final double currentPosition, final double speed, final double timeIncrement) {
        final double distanceToTravel = timeIncrement * speed;
        final double newPosition = currentPosition + distanceToTravel;
        final double oldFloor = Math.floor(currentPosition);
        final double nextFloor = Math.floor(newPosition);

        return Math.abs(nextFloor - oldFloor) > DELTA_ALLOWED;
    }

    public static final double calculateElapsedTimeWhenCrossingFloorsAscending(final double currentPosition, final double speed, final double timeIncrement) {
        final double distanceToTravel = timeIncrement * speed;
        final double newPosition = currentPosition + distanceToTravel;
        final double oldFloor = Math.floor(currentPosition);
        final double nextFloor = Math.floor(newPosition);

        if (Math.abs(nextFloor - oldFloor) < DELTA_ALLOWED) {
            return Double.NaN;
        }

        return (nextFloor - currentPosition) * timeIncrement / distanceToTravel;
    }

    public static final boolean didCrossFloorDescending(final double currentPosition, final double speed, final double timeIncrement) {
        final double distanceToTravel = timeIncrement * speed;
        final double newPosition = currentPosition - distanceToTravel;
        final double oldFloor = Math.ceil(currentPosition);
        final double nextFloor = Math.ceil(newPosition);

        return Math.abs(nextFloor - oldFloor) > DELTA_ALLOWED;
    }

    public static final double calculateElapsedTimeWhenCrossingFloorsDescending(final double currentPosition, final double speed, final double timeIncrement) {
        final double distanceToTravel = timeIncrement * speed;
        final double newPosition = currentPosition - distanceToTravel;
        final double oldFloor = Math.ceil(currentPosition);
        final double nextFloor = Math.ceil(newPosition);

        if (Math.abs(nextFloor - oldFloor) < DELTA_ALLOWED) {
            return Double.NaN;
        }

        return (currentPosition - nextFloor) * timeIncrement / distanceToTravel;
    }


    public double getTargetFloor() {
        return targetFloor;
    }

    public double getPickupTimeRequired() {
        return pickupTimeRequired;
    }

    public void setPickupTimeRequired(double pickupTimeRequired) {
        this.pickupTimeRequired = pickupTimeRequired;
    }

    public double getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(double currentPosition) {
        this.currentPosition = currentPosition;
    }

    public int getMaxWeight() {
        return maxWeight;
    }

    public void setMaxWeight(int maxWeight) {
        this.maxWeight = maxWeight;
    }

    public int getMaxArea() {
        return maxArea;
    }

    public void setMaxArea(int maxArea) {
        this.maxArea = maxArea;
    }

    public void setState(final State state) {
        if (state == this.state) {
            throw new IllegalArgumentException("Attempted to set elevator with same state");
        }

        this.state = state;
        timeInCurrentState = 0.0;
    }

    public String getName() {
        return name;
    }

    public void setSpeed(final double newSpeed) {
        speed = newSpeed;
    }

    public double getSpeed() {
        return speed;
    }

    public State getState() {
        return state;
    }

    public void setTimeInCurrentState(double newTimeInCurrentState) {
        timeInCurrentState = newTimeInCurrentState;
    }

    public void incrementTimeInCurrentState(double increment) {
        timeInCurrentState += increment;
        currentTimestamp += increment;
    }

    public double getTimeInCurrentState() {
        return timeInCurrentState;
    }

    public double getCurrentTimestamp() {
        return currentTimestamp;
    }

    public void addPickupRequest(final PickupRequest pickupRequest) {
        listActiveRequests.add(pickupRequest);
    }

    /**
     * Will advance the simulation of this elevator by a given amount of time.  Internally, this may be a
     * recursive call as it transitions the elevator through each state, decreasing the total amount of time
     * left before transitioning into a new state and repeating until it settles in an end state.
     *
     * @param timeIncrement the amount of time to advance the simulation by
     */
    public abstract void incrementTime(final double timeIncrement);

    public final List<PickupRequest> getActiveRequests() {
        return listActiveRequests;
    }

    public final List<PickupRequest> getServicedRequests() {
        return listServicedRequests;
    }
}
