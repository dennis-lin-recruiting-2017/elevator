package com.dennis.interviews.elevators.scheduler;

import java.util.List;

import com.dennis.interviews.elevators.AbstractElevator;
import com.dennis.interviews.elevators.AbstractElevatorScheduler;

public class OpportunisticScheduler extends AbstractElevatorScheduler {
    @Override
    protected void scheduleIdleElevators(List<AbstractElevator> listIdleElevators, List<Integer> activeFloors) {
        //  TODO:  Schedule the elevators to maximize elevator usage (i.e. minimize elevators traveling empty)
        throw new RuntimeException("Not implemented yet.");
    }
}
