package com.dennis.interviews.elevators.scheduler;

import java.util.List;
import java.util.Set;

import com.dennis.interviews.elevators.AbstractElevator;
import com.dennis.interviews.elevators.Simulation;

public class RoundRobinScheduler extends AbstractElevatorScheduler {
    public RoundRobinScheduler(Simulation simulation) {
        super(simulation);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void scheduleIdleElevators(List<AbstractElevator> listIdleElevators, Set<Integer> activeFloors) {
        // TODO Auto-generated method stub

    }
}
