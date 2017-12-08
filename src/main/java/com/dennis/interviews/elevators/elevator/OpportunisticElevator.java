package com.dennis.interviews.elevators.elevator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dennis.interviews.elevators.AbstractElevator;

public class OpportunisticElevator extends AbstractElevator {
    private static final Logger LOG = LoggerFactory.getLogger(OpportunisticElevator.class);
    public OpportunisticElevator(final String name) {
        super(name);
    }

    @Override
    public void processStateAscendingCrossFloors(final double newPosition, final double nextFloor, final double timeIncrement, final double timeElapsedUntilCrossingFloors) {
        openElevatorDoors(newPosition, nextFloor, timeIncrement, timeElapsedUntilCrossingFloors);
    }

    @Override
    public void processStateDescendingCrossFloors(final double newPosition, final double nextFloor, final double timeIncrement, final double timeElapsedUntilCrossingFloors) {
        openElevatorDoors(newPosition, nextFloor, timeIncrement, timeElapsedUntilCrossingFloors);
    }

    @Override
    public void processStateLoading(double timeIncrement) {
        double newTimeInCurrentState = getTimeInCurrentState() + timeIncrement;

        if (newTimeInCurrentState < getPickupTimeRequired()) {
            incrementTimeInCurrentState(timeIncrement);
            return;
        }

        double timeAfterFinishedLoading = newTimeInCurrentState - getPickupTimeRequired();
        if (getTargetFloor() > getCurrentPosition()) {
            setState(State.ASCENDING);
        } else if (getTargetFloor() < getCurrentPosition()) {
            setState(State.DESCENDING);
        } else {
            setState(State.IDLE);
        }

        incrementTime(timeAfterFinishedLoading);
    }

    @Override
    public void processStateIdle(double timeIncrement) {
        incrementTimeInCurrentState(timeIncrement);
    }
}
