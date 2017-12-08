package com.dennis.interviews.elevators;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

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
    }

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

    @SuppressWarnings("unused")
    private AbstractElevator() {
        throw new RuntimeException("Should not use default constructor");
    }

    public AbstractElevator(final String name) {
        this.name = name;
    }

    /**
     * Sets the simulation that this elevator is associated with. (NOTE:  Package visibility so only the abstract
     * objects can set this value)
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

    public void setMinFloor(final double minFloor) {
        this.minFloor = minFloor;
    }

    public double getMaxFloor() {
        return maxFloor;
    }

    public void setMaxFloor(final double maxFloor) {
        this.maxFloor = maxFloor;
    }

    public void setTargetFloor(final double newTargetFloor) {
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

    public static final boolean didCrossFloorAscending(final double currentPosition, final double speed,
            final double timeIncrement) {
        final double distanceToTravel = timeIncrement * speed;
        final double newPosition = currentPosition + distanceToTravel;
        final double oldFloor = Math.floor(currentPosition);
        final double nextFloor = Math.floor(newPosition);

        return Math.abs(nextFloor - oldFloor) > DELTA_ALLOWED;
    }

    public static final double calculateElapsedTimeWhenCrossingFloorsAscending(final double currentPosition,
            final double speed, final double timeIncrement) {
        final double distanceToTravel = timeIncrement * speed;
        final double newPosition = currentPosition + distanceToTravel;
        final double oldFloor = Math.floor(currentPosition);
        final double nextFloor = Math.floor(newPosition);

        if (Math.abs(nextFloor - oldFloor) < DELTA_ALLOWED) {
            return Double.NaN;
        }

        return (nextFloor - currentPosition) * timeIncrement / distanceToTravel;
    }

    public static final boolean didCrossFloorDescending(final double currentPosition, final double speed,
            final double timeIncrement) {
        final double distanceToTravel = timeIncrement * speed;
        final double newPosition = currentPosition - distanceToTravel;
        final double oldFloor = Math.ceil(currentPosition);
        final double nextFloor = Math.ceil(newPosition);

        return Math.abs(nextFloor - oldFloor) > DELTA_ALLOWED;
    }

    public static final double calculateElapsedTimeWhenCrossingFloorsDescending(final double currentPosition,
            final double speed, final double timeIncrement) {
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

    public final int getMaxWeight() {
        return maxWeight;
    }

    public final void setMaxWeight(int maxWeight) {
        this.maxWeight = maxWeight;
    }

    public final int getMaxArea() {
        return maxArea;
    }

    public final void setMaxArea(int maxArea) {
        this.maxArea = maxArea;
    }

    /**
     * Sets the state of the elevator and resets the timer for how long the elevator has been in the current state.
     *
     * @param state the new state of the elevator
     */
    public final void setState(final State state) {
        if (state == this.state) {
            throw new IllegalArgumentException("Attempted to set elevator with same state");
        }

        LOG.info("Elevator({}) changed state from {}({}) to {} at timestamp={}",
                new Object[] { name, this.state, timeInCurrentState, state, currentTimestamp });

        this.state = state;
        timeInCurrentState = 0.0;
    }

    public final String getName() {
        return name;
    }

    public final void setSpeed(final double newSpeed) {
        speed = newSpeed;
    }

    public final double getSpeed() {
        return speed;
    }

    public final State getState() {
        return state;
    }

    public final void setTimeInCurrentState(double newTimeInCurrentState) {
        timeInCurrentState = newTimeInCurrentState;
    }

    public final void incrementTimeInCurrentState(double increment) {
        timeInCurrentState += increment;
        currentTimestamp += increment;
    }

    public final double getTimeInCurrentState() {
        return timeInCurrentState;
    }

    public final double getCurrentTimestamp() {
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
    public final void incrementTime(final double timeIncrement) {
        LOG.info("Elevator({})::increment() ; - processing '{}' state at timeInCurrentState={}, with timeIncrement={}",
                new Object[] {getName(), getState(), getTimeInCurrentState(), timeIncrement });
        /**
         * I hate switch statements -- easy to forget the terminating break statement.
         */
        if (State.ASCENDING == getState()) {
            processStateAscending(timeIncrement);
        } else if (State.DESCENDING == getState()) {
            processStateDescending(timeIncrement);
        } else if (State.LOADING == getState()) {
            processStateLoading(timeIncrement);
        } else if (State.IDLE == getState()) {
            processStateIdle(timeIncrement);
        } else {
            throw new IllegalArgumentException("Elevator is in invalid or unhandled state: " + getState());
        }
    }

    /**
     * Common function for simulating an ascending elevator.
     * @param timeIncrement the amount of time to advance the simulation by
     */
    private void processStateAscending(final double timeIncrement) {
        final double distanceToTravel = timeIncrement * getSpeed();
        final double newPosition = getCurrentPosition() + distanceToTravel;
        final double nextFloor = Math.floor(newPosition);

        //  Never crossed a floor -- no special handling needed
        if (!AbstractElevator.didCrossFloorAscending(getCurrentPosition(), getSpeed(), timeIncrement)) {
            incrementTimeInCurrentState(timeIncrement);
            setCurrentPosition(newPosition);

            return;
        }

        //  We just crossed the a new floor!
        //  1.  First calculate the time we crossed the floor and increment the timestamp of the elevator.
        final double timeElapsedUntilCrossingFloors =
                AbstractElevator.calculateElapsedTimeWhenCrossingFloorsAscending(
                        getCurrentPosition(), getSpeed(), timeIncrement);

        processStateAscendingCrossFloors(newPosition, nextFloor, timeIncrement, timeElapsedUntilCrossingFloors);
    }

    /**
     * Simulates what an elevator would do as it crosses a floor when ascending (maybe open its doorsand let some
     * passengers out?).
     * @param newPosition the expected new position of the elevator at the end of the time increment
     * @param nextFloor the floor that was crossed
     * @param timeIncrement the amount of time to advance the simulation by
     * @param timeElapsedUntilCrossingFloors the time elapsed when the floor will be crossed
     */
    protected abstract void processStateAscendingCrossFloors(final double newPosition, final double nextFloor,
            final double timeIncrement, final double timeElapsedUntilCrossingFloors);


    /**
     * Common function for simulating a descending elevator.
     * @param timeIncrement the time to increment the simulation by
     */
    private void processStateDescending(final double timeIncrement) {
        final double distanceToTravel = timeIncrement * getSpeed();
        final double newPosition = getCurrentPosition() - distanceToTravel;
        final double nextFloor = Math.ceil(newPosition);

        //  Never crossed a floor -- no special handling needed
        if (!AbstractElevator.didCrossFloorDescending(getCurrentPosition(), getSpeed(), timeIncrement)) {
            incrementTimeInCurrentState(timeIncrement);
            setCurrentPosition(newPosition);

            return;
        }

        //  We just crossed the a new floor!
        //  1.  First calculate the time we crossed the floor and increment the timestamp of the elevator.
        final double timeElapsedUntilCrossingFloors =
                AbstractElevator.calculateElapsedTimeWhenCrossingFloorsDescending(
                        getCurrentPosition(), getSpeed(), timeIncrement);

        processStateDescendingCrossFloors(newPosition, nextFloor, timeIncrement, timeElapsedUntilCrossingFloors);
    }

    /**
     * Simulates what an elevator would do as it crosses a floor when descending (maybe open its doors and let some
     * passengers out?).
     * @param newPosition the expected new position of the elevator at the end of the time increment
     * @param nextFloor the floor that was crossed
     * @param timeIncrement the amount of time to advance the simulation by
     * @param timeElapsedUntilCrossingFloors the time elapsed when the floor will be crossed
     */
    protected abstract void processStateDescendingCrossFloors(final double newPosition, final double nextFloor,
            final double timeIncrement, final double timeElapsedUntilCrossingFloors);

    /**
     * Simulates what the elevator would do while it is waiting for passengers to enter/exit.
     * @param timeIncrement the amount of time to advance the simulation by
     */
    protected abstract void processStateLoading(final double timeIncrement);

    /**
     *  Simulates what the elevator would do if it finds itself in an idle state (maybe re-position itself?).
     */
    protected abstract void processStateIdle(final double timeIncrement);

    public final List<PickupRequest> getActiveRequests() {
        return listActiveRequests;
    }

    public final List<PickupRequest> getServicedRequests() {
        return listServicedRequests;
    }


    /**
     * Simulating the opening of elevator doors, i.e. passengers leaving and entering the elevator.
     * @param newPosition the expected new position of the elevator at the end of the time increment
     * @param nextFloor the floor that was crossed
     * @param timeIncrement the amount of time to advance the simulation by
     * @param timeElapsedUntilCrossingFloors the time elapsed when the floor will be crossed
     */
    protected final void openElevatorDoors(final double newPosition, final double nextFloor,
            final double timeIncrement, final double timeElapsedUntilCrossingFloors) {
        final int iNextFloor = (int) nextFloor;
        incrementTimeInCurrentState(timeElapsedUntilCrossingFloors);
        final double timeInNewState = timeIncrement - timeElapsedUntilCrossingFloors;
        final double timestampCrossingFloors = getCurrentTimestamp() + timeElapsedUntilCrossingFloors;
        setCurrentPosition(nextFloor);

        //  2.  First check to see if anybody is getting off on the new floor.
        Set<PickupRequest> setDepartingRiders = new HashSet<>();
        for (PickupRequest pickupRequest : getActiveRequests()) {
            if (pickupRequest.getTargetFloor() == iNextFloor) {
                setDepartingRiders.add(pickupRequest);
            }
        }

        //  3.  Next, check to see if anybody wanted to get on the floor at the time.
        List<PickupRequest> listPickupRequestsAtFloor = null;
        if (null != getSimulation()) {
            listPickupRequestsAtFloor = getSimulation().getMapActiveRequestsByFloor().get(iNextFloor);
        }

        Set<PickupRequest> newRiders = new HashSet<>();
        while ((null != listPickupRequestsAtFloor) && !listPickupRequestsAtFloor.isEmpty()) {
            if (getMaxWeight() <= getActiveRequests().size() + newRiders.size() - setDepartingRiders.size()) {
                break;
            }
            PickupRequest pickupRequest = listPickupRequestsAtFloor.get(0);
            if (pickupRequest.getTimestamp() < timestampCrossingFloors) {
                pickupRequest = listPickupRequestsAtFloor.remove(0);
                //  Pickup time is when the elevator doors has closed.
                pickupRequest.setTimestampPickup(timestampCrossingFloors + getPickupTimeRequired());
                newRiders.add(pickupRequest);
            } else {
                break;
            }
        }

        if (setDepartingRiders.isEmpty() && newRiders.isEmpty()) {
            if ((State.ASCENDING == getState()) ? getTargetFloor() > nextFloor : getTargetFloor() < nextFloor) {
                setCurrentPosition(newPosition);
                incrementTimeInCurrentState(timeIncrement);
            } else {
                incrementTimeInCurrentState(timeElapsedUntilCrossingFloors);

                setCurrentPosition(nextFloor);
                setState(State.IDLE);
                incrementTimeInCurrentState(timeIncrement - timeElapsedUntilCrossingFloors);
            }
            return;
        }

        //  At the time we cross the floor, we want to drop off all the riders who are getting off...
        incrementTimeInCurrentState(timeElapsedUntilCrossingFloors);
        for (PickupRequest pickupRequest : setDepartingRiders) {
            pickupRequest.setTimestampDropoff(timestampCrossingFloors);
            getServicedRequests().add(pickupRequest);
            getActiveRequests().remove(pickupRequest);
        }

        // ... and pick up the new ones who are waiting
        for (PickupRequest newRider : newRiders) {
            if (currentPosition < newPosition) {
                setTargetFloor(Math.max(newRider.getTargetFloor(), getTargetFloor()));
            } else {
                setTargetFloor(Math.min(newRider.getTargetFloor(), getTargetFloor()));
            }
            addPickupRequest(newRider);
        }

        //  Then we increment the loading times
        setState(State.LOADING);
        incrementTimeInCurrentState(timeInNewState);
    }
}
