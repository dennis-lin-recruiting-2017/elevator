package com.dennis.interviews.elevators.scheduler;

import java.util.List;

import com.dennis.interviews.elevators.AbstractElevator;
import com.dennis.interviews.elevators.AbstractElevatorScheduler;

public class GreedyMinimizeEmptyElevatorScheduler extends AbstractElevatorScheduler {
    @Override
    protected void scheduleIdleElevators(List<AbstractElevator> listIdleElevators, List<Integer> activeFloors) {
        for (AbstractElevator idleElevator : listIdleElevators) {
            if (activeFloors.isEmpty()) {
                break;
            }

            int minDistance = Integer.MAX_VALUE;
            int indexOfClosestFloor = Integer.MIN_VALUE;
            int elevatorFloor = (int) idleElevator.getCurrentPosition();

            for (int counter = 0; counter < activeFloors.size(); counter++) {
                Integer floor = activeFloors.get(counter);
                int distance = Math.abs(elevatorFloor - floor.intValue());
                if (distance < minDistance) {
                    minDistance = distance;
                    indexOfClosestFloor = counter;
                }
            }

            Integer closestFloor = activeFloors.remove(indexOfClosestFloor);

            idleElevator.setTargetFloor(closestFloor.doubleValue());
        }
    }
}
