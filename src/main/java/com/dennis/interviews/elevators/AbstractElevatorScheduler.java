package com.dennis.interviews.elevators;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractElevatorScheduler {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractElevatorScheduler.class);

    private Simulation simulation;

    final void setSimulation(final Simulation newSimulation) {
        simulation = newSimulation;
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

        final List<Integer> listFloorsToSchedule = new ArrayList<>();
        final double checkTimestamp = currentTimestamp + timeIncrement;
        for (Map.Entry<Integer, List<PickupRequest>> entry : getSimulation().getMapActiveRequestsByFloor().entrySet()) {
            if ((null != entry.getValue()) && !entry.getValue().isEmpty()) {
                if (entry.getValue().get(0).getTimestamp() < checkTimestamp) {
                    listFloorsToSchedule.add(entry.getKey());
                }
            }
        }

        LOG.info("{}::scheduleElevators(currentTimestamp={}, timeIncrement={}) "
                    + "found idle elevators({}) and active floors({})",
                new Object[] { getClass().getName(), currentTimestamp, timeIncrement, listIdleElevators,
                        listFloorsToSchedule });
        scheduleIdleElevators(listIdleElevators, listFloorsToSchedule);
    }

    /**
     * Matches idle elevators with active floors (floors with waiting passengers).
     *
     * @param listIdleElevators the list of elevators to schedule
     * @param activeFloors the set of floors with people waiting
     */
    protected abstract void scheduleIdleElevators(final List<AbstractElevator> listIdleElevators,
            final List<Integer> activeFloors);

    protected final Simulation getSimulation() {
        return simulation;
    }
}
