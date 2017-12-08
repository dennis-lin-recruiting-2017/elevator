package com.dennis.interviews.elevators.scheduler;

import java.util.List;

import com.dennis.interviews.elevators.AbstractElevator;
import com.dennis.interviews.elevators.AbstractElevatorScheduler;

public class FirstComeFirstServeScheduler extends AbstractElevatorScheduler {
    @Override
    protected void scheduleIdleElevators(List<AbstractElevator> listIdleElevators, List<Integer> activeFloors) {
        throw new RuntimeException("Not implemented yet.");
    }
}
