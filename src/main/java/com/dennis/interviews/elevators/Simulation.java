package com.dennis.interviews.elevators;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dennis.interviews.elevators.elevator.OpportunisticElevator;
import com.dennis.interviews.elevators.pickup.AbstractPickupRequestGenerator;
import com.dennis.interviews.elevators.pickup.RegularIntervalGenerator;

/**
 * First pass at creating a simulation.  I did not get an opportunity to re-factor out the code (please see
 * the AbstractSimulation.java class in the same package).
 *
 * @author dennislin
 */
public final class Simulation {
    public static enum State {
        NOT_STARTED,
        RUNNING,
        FINISHED;
    }

    private static final Logger LOG = LoggerFactory.getLogger(Simulation.class);

    private final List<AbstractElevator> listElevators = new ArrayList<>();
    private final List<PickupRequest> listPickupRequests;
    private final AbstractElevatorScheduler scheduler;
    private final Map<Integer, List<PickupRequest>> mapActiveRequestsByFloor = new HashMap<>();

    private State state = State.NOT_STARTED;
    private static final double TIMESTAMP_INCREMENT = 1.0;
    private double currentTimestamp = 0.0;

    @SuppressWarnings("unused")
    private Simulation() {
        throw new RuntimeException("Should not call default constructor.");
    }

    /**
     * Constructor used for unit testing the simulation engine (does not need a schedule).
     *
     * @param elevators the elevators to simulate
     * @param pickupRequests the pickup requests / passengers to service
     */
    public Simulation(final List<AbstractElevator> elevators, final List<PickupRequest> pickupRequests) {
        this.scheduler = null;
        listPickupRequests = pickupRequests;
        initialize(elevators);
    }

    /**
     * Constructor used for unit testing the simulation engine.
     *
     * @param elevators the elevators to simulate
     * @param pickupRequests the pickup requests / passengers to service
     * @param scheduler the elevator scheduler to use to schedule idle elevators
     */
    public Simulation(final List<AbstractElevator> elevators, final List<PickupRequest> pickupRequests,
            final AbstractElevatorScheduler scheduler) {
        this.scheduler = scheduler;
        listPickupRequests = pickupRequests;
        initialize(elevators);
    }

    private void initialize(final List<AbstractElevator> elevators) {
        for (AbstractElevator elevator : elevators) {
            elevator.setSimulation(this);
            listElevators.add(elevator);
        }

        for (PickupRequest pickupRequest : listPickupRequests) {
            if (null == mapActiveRequestsByFloor.get(pickupRequest.getStartingFloor())) {
                mapActiveRequestsByFloor.put(pickupRequest.getStartingFloor(), new LinkedList<>());
            }

            mapActiveRequestsByFloor.get(pickupRequest.getStartingFloor()).add(pickupRequest);
        }

        if (null != scheduler) {
            scheduler.setSimulation(this);
        }
    }

    public Simulation(final int numElevators, final List<PickupRequest> pickupRequests) {
        for (int counter = 0; counter < numElevators; counter++) {
            listElevators.add(new OpportunisticElevator(String.format("Elevator-%d", counter + 1)));
        }

        listPickupRequests = pickupRequests;
        this.scheduler = null;
    }

    public final void simulate() {
        if (currentTimestamp > 0.0) {
            throw new IllegalStateException("Simulation has already been started -- can not restart.");
        }

        state = State.RUNNING;

        while (State.FINISHED != state) {
            incrementTime(TIMESTAMP_INCREMENT);
        }
    }

    public final State getState() {
        return state;
    }

    public final void incrementTime(final double timeIncrement) {
        if (State.FINISHED == state) {
            throw new IllegalStateException("Simulation has already finished!");
        }

        double newTimestamp = currentTimestamp + timeIncrement;
        if (null != scheduler) {
            scheduler.scheduleElevators(currentTimestamp, timeIncrement);
        }

        //  1.  increment the time until the next pickup request
        for (AbstractElevator elevator : listElevators) {
            elevator.incrementTime(timeIncrement);
        }

        currentTimestamp = newTimestamp;

        if (!hasPickupRequestsRemaining() && areAllElevatorsIdle()) {
            LOG.info("Simulation ended with timestamp={}", currentTimestamp);
            state = State.FINISHED;
        }
    }

    private boolean hasPickupRequestsRemaining() {
        boolean remaining = false;
        for (Map.Entry<Integer, List<PickupRequest>> entry : mapActiveRequestsByFloor.entrySet()) {
            remaining = remaining || (!entry.getValue().isEmpty());
        }

        return remaining;
    }

    private final boolean areAllElevatorsIdle() {
        boolean isIdle = true;
        for (AbstractElevator elevator : listElevators) {
            isIdle = isIdle && (elevator.getState() == AbstractElevator.State.IDLE);
        }

        return isIdle;
    }

    public final double getCurrentTimestamp() {
        return currentTimestamp;
    }

    public final Map<Integer, List<PickupRequest>> getMapActiveRequestsByFloor() {
        return mapActiveRequestsByFloor;
    }

    /**
     * Convenient function to search for all the idle elevators at the current timestamp.
     *
     * @return the list of idle elevators at the current timestamp
     */
    public final List<AbstractElevator> getIdleElevators() {
        List<AbstractElevator> listIdleElevators = new ArrayList<>();
        for (AbstractElevator elevator : listElevators) {
            if (elevator.getState() == AbstractElevator.State.IDLE) {
                listIdleElevators.add(elevator);
            }
        }

        return listIdleElevators;
    }

    /**
     * The one and only main function.
     * @param args command-line arguments
     */
    public static final void main(final String[] args) {
        final double experimentDurationInSeconds = 20000.0;
        final AbstractPickupRequestGenerator pickupRequestGenerator =
                new RegularIntervalGenerator(0, 1, experimentDurationInSeconds);
        List<PickupRequest> sortedRequests = pickupRequestGenerator.generatePickupRequests(20);

        Simulation simulation = new Simulation(1, sortedRequests);
        simulation.simulate();
    }
}
