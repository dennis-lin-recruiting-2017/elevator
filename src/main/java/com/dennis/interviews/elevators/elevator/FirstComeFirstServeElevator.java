package com.dennis.interviews.elevators.elevator;

import com.dennis.interviews.elevators.AbstractElevator;

public class FirstComeFirstServeElevator extends AbstractElevator {
    public FirstComeFirstServeElevator(String name) {
        super(name);
    }

    @Override
    protected void processStateAscending(double timeIncrement) {
        throw new RuntimeException("Not implemented yet.");
    }

    @Override
    protected void processStateDescending(double timeIncrement) {
        throw new RuntimeException("Not implemented yet.");
    }

    @Override
    protected void processStateLoading(double timeIncrement) {
        throw new RuntimeException("Not implemented yet.");
    }

    @Override
    protected void processStateIdle(double timeIncrement) {
        throw new RuntimeException("Not implemented yet.");
    }
}
