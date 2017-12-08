package com.dennis.interviews.elevators.scenarios;

import java.util.ArrayList;
import java.util.List;

import com.dennis.interviews.elevators.AbstractElevator;
import com.dennis.interviews.elevators.AbstractElevatorScheduler;
import com.dennis.interviews.elevators.PickupRequest;
import com.dennis.interviews.elevators.Simulation;
import com.dennis.interviews.elevators.elevator.OpportunisticElevator;
import com.dennis.interviews.elevators.pickup.AbstractPickupRequestGenerator;
import com.dennis.interviews.elevators.pickup.RegularIntervalGenerator;
import com.dennis.interviews.elevators.scheduler.RoundRobinScheduler;

public class SimulationScenario01 {
    /**
     * The one and only main function.
     *
     * @param args command-line arguments.
     */
    public static final void main(final String[] args) {
        final double experimentDurationInSeconds = 20000.0;
        final AbstractPickupRequestGenerator pickupRequestGenerator =
                new RegularIntervalGenerator(0, 10, experimentDurationInSeconds);
        List<PickupRequest> sortedRequests = pickupRequestGenerator.generatePickupRequests(20);

        List<AbstractElevator> listElevators = new ArrayList<>();
        listElevators.add(new OpportunisticElevator("test1"));
        listElevators.add(new OpportunisticElevator("test2"));
        listElevators.add(new OpportunisticElevator("test3"));

        AbstractElevatorScheduler scheduler = new RoundRobinScheduler();

        Simulation simulation = new Simulation(listElevators, sortedRequests, scheduler);
        simulation.simulate();
    }
}
