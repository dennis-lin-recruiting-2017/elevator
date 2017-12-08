package com.dennis.interviews.elevators.scheduler;

import java.util.List;

import com.dennis.interviews.elevators.AbstractElevator;
import com.dennis.interviews.elevators.AbstractElevatorScheduler;

public class GreedyScheduler extends AbstractElevatorScheduler {
    @Override
    protected void scheduleIdleElevators(List<AbstractElevator> listIdleElevators, List<Integer> activeFloors) {
        for (AbstractElevator idleElevator : listIdleElevators) {
            if (activeFloors.isEmpty()) {
                break;
            }

            idleElevator.setTargetFloor(activeFloors.remove(0).doubleValue());
        }
    }
}
