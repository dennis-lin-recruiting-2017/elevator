package com.dennis.interviews.elevators.elevator;

import com.dennis.interviews.elevators.AbstractElevator;

/**
 *  Implements the behavior seen in most elevators, i.e. opens doors for passengers in the carriage who may have
 *  different final destinations, and picking up passengers en route to a final destination.
 *
 *  @author dennislin
 */
public class OpportunisticElevator extends AbstractElevator {
    public OpportunisticElevator(final String name) {
        super(name);
    }

    @Override
    public void processStateAscendingCrossFloors(final double newPosition, final double nextFloor,
            final double timeIncrement, final double timeElapsedUntilCrossingFloors) {
        openElevatorDoors(newPosition, nextFloor, timeIncrement, timeElapsedUntilCrossingFloors);
    }

    @Override
    public void processStateDescendingCrossFloors(final double newPosition, final double nextFloor,
            final double timeIncrement, final double timeElapsedUntilCrossingFloors) {
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
