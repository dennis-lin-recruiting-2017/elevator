package com.dennis.interviews.elevators.scheduler;

import java.util.List;

import com.dennis.interviews.elevators.AbstractElevator;
import com.dennis.interviews.elevators.AbstractElevatorScheduler;

public class LatencyOptimizingScheduler extends AbstractElevatorScheduler {
    @Override
    protected void scheduleIdleElevators(List<AbstractElevator> listIdleElevators, List<Integer> activeFloors) {
        //  Send each elevator to the closest active floor.
        throw new RuntimeException("Not implemented yet.");
    }
}
