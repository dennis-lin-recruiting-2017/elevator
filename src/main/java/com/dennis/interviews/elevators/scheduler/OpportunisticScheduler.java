package com.dennis.interviews.elevators.scheduler;

import java.util.List;
import java.util.Set;

import com.dennis.interviews.elevators.AbstractElevator;
import com.dennis.interviews.elevators.Simulation;

public class OpportunisticScheduler extends AbstractElevatorScheduler {
    public OpportunisticScheduler(final Simulation simulation) {
        super(simulation);
    }

    @Override
    protected void scheduleIdleElevators(List<AbstractElevator> listIdleElevators, Set<Integer> activeFloors) {
    }
}
