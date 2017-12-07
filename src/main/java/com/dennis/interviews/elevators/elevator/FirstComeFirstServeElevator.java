package com.dennis.interviews.elevators.elevator;

import com.dennis.interviews.elevators.AbstractElevator;

public class FirstComeFirstServeElevator extends AbstractElevator {
    public FirstComeFirstServeElevator(String name) {
        super(name);
    }

    @Override
    public void incrementTime(double timeIncrement) {
    }
}
