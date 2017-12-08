package com.dennis.interviews.elevators.scheduler;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.dennis.interviews.elevators.AbstractElevator;
import com.dennis.interviews.elevators.PickupRequest;
import com.dennis.interviews.elevators.Simulation;

public abstract class AbstractElevatorScheduler {
    @SuppressWarnings("unused")
    private AbstractElevatorScheduler() {
        throw new RuntimeException("Should not call default constructor.");
    }

    private final Simulation simulation;

    public AbstractElevatorScheduler(final Simulation simulation) {
        this.simulation = simulation;
    }

    /**
     * Takes a look at all the idle elevators and schedules them based on the current demand.
     */
    public final void scheduleElevators(final double currentTimestamp, final double timeIncrement) {
        List<AbstractElevator> listIdleElevators = getSimulation().getIdleElevators();

        //  No idle elevators, so nothing to do.
        if ((null == listIdleElevators) || listIdleElevators.isEmpty()) {
            return;
        }

        final Set<Integer> setFloorsToSchedule = new HashSet<>();
        final double checkTimestamp = currentTimestamp + timeIncrement;
        for (Map.Entry<Integer, List<PickupRequest>> entry : getSimulation().getMapActiveRequestsByFloor().entrySet()) {
            if ((null != entry.getValue()) && !entry.getValue().isEmpty()) {
                if (entry.getValue().get(0).getTimestamp() < checkTimestamp) {
                    setFloorsToSchedule.add(entry.getKey());
                }
            }
        }

        scheduleIdleElevators(listIdleElevators, setFloorsToSchedule);
    }

    /**
     * @param listIdleElevators the list of elevators to schedule
     * @param activeFloors the set of floors with people waiting
     */
    protected abstract void scheduleIdleElevators(final List<AbstractElevator> listIdleElevators, final Set<Integer> activeFloors);

    protected final Simulation getSimulation() {
        return simulation;
    }
}
