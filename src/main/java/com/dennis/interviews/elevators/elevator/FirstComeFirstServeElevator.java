package com.dennis.interviews.elevators.elevator;

import com.dennis.interviews.elevators.AbstractElevator;

/**
 * Can only be scheduled after it becomes empty.
 *
 * @author dennislin
 *
 */
public class FirstComeFirstServeElevator extends AbstractElevator {
    public FirstComeFirstServeElevator(String name) {
        super(name);
    }

    @Override
    protected void processStateLoading(double timeIncrement) {
        throw new RuntimeException("Not implemented yet.");
    }

    @Override
    protected void processStateIdle(double timeIncrement) {
        throw new RuntimeException("Not implemented yet.");
    }

    @Override
    protected void processStateAscendingCrossFloors(double newPosition, double nextFloor, double timeIncrement,
            double timeElapsedUntilCrossingFloors) {
        throw new RuntimeException("Not implemented yet.");
    }

    @Override
    protected void processStateDescendingCrossFloors(double newPosition, double nextFloor, double timeIncrement,
            double timeElapsedUntilCrossingFloors) {
        throw new RuntimeException("Not implemented yet.");
    }
}
