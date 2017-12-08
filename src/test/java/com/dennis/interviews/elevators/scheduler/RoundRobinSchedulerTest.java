package com.dennis.interviews.elevators.scheduler;

import java.util.LinkedList;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.dennis.interviews.elevators.AbstractElevator;
import com.dennis.interviews.elevators.AbstractElevatorScheduler;
import com.dennis.interviews.elevators.PickupRequest;
import com.dennis.interviews.elevators.Simulation;
import com.dennis.interviews.elevators.elevator.OpportunisticElevator;


public class RoundRobinSchedulerTest extends AbstractSchedulerTest {
    @Test
    public void testScheduleOneElevator() {
        AbstractElevator testElevator = new OpportunisticElevator("test1");
        testElevator.setCurrentPosition(1.0);
        List<AbstractElevator> listElevators = new LinkedList<>();
        listElevators.add(testElevator);

        List<PickupRequest> sortedRequests = new LinkedList<>();
        PickupRequest pickupRequest = new PickupRequest(2, 2, 1.0); // Start on 2nd floor, go up 2 floors, show up at t=100.
        sortedRequests.add(pickupRequest);
        pickupRequest = new PickupRequest(2, -2, 75.0); // Start on 2nd floor, go up 2 floors, show up at t=100.
        sortedRequests.add(pickupRequest);

        AbstractElevatorScheduler scheduler = new RoundRobinScheduler();
        Simulation simulation = new Simulation(listElevators, sortedRequests, scheduler);

        simulation.incrementTime(9.0);      // time = 9.0
        Assert.assertEquals(testElevator.getCurrentPosition(), 1.9, DELTA_ALLOWED);
        Assert.assertEquals(testElevator.getState(), AbstractElevator.State.ASCENDING);
        Assert.assertEquals(testElevator.getActiveRequests().size(), 0);

        simulation.incrementTime(2.0);      // time = 11.0
        Assert.assertEquals(testElevator.getCurrentPosition(), 2.0, DELTA_ALLOWED);
        Assert.assertEquals(testElevator.getState(), AbstractElevator.State.LOADING);
        Assert.assertEquals(testElevator.getActiveRequests().size(), 1);

        simulation.incrementTime(15.0);     // time = 26.0
        Assert.assertEquals(testElevator.getCurrentPosition(), 2.1, DELTA_ALLOWED);
        Assert.assertEquals(testElevator.getState(), AbstractElevator.State.ASCENDING);
        Assert.assertEquals(testElevator.getActiveRequests().size(), 1);

        simulation.incrementTime(15.0);     // time = 41.0
        Assert.assertEquals(testElevator.getCurrentPosition(), 3.6, DELTA_ALLOWED);
        Assert.assertEquals(testElevator.getState(), AbstractElevator.State.ASCENDING);
        Assert.assertEquals(testElevator.getActiveRequests().size(), 1);

        simulation.incrementTime(5.0);      // time = 46.0
        Assert.assertEquals(testElevator.getCurrentPosition(), 4.0, DELTA_ALLOWED);
        Assert.assertEquals(testElevator.getState(), AbstractElevator.State.LOADING);
        Assert.assertEquals(testElevator.getActiveRequests().size(), 0);

        simulation.incrementTime(15.0);     // time = 61.0
        Assert.assertEquals(testElevator.getCurrentPosition(), 4.0, DELTA_ALLOWED);
        Assert.assertEquals(testElevator.getState(), AbstractElevator.State.IDLE);
        Assert.assertEquals(testElevator.getActiveRequests().size(), 0);

        simulation.incrementTime(13.5);     // time = 74.5
        Assert.assertEquals(testElevator.getCurrentPosition(), 4.0, DELTA_ALLOWED);
        Assert.assertEquals(testElevator.getState(), AbstractElevator.State.IDLE);
        Assert.assertEquals(testElevator.getActiveRequests().size(), 0);

        //  New rider arrived on floor 2
        simulation.incrementTime(0.5);     // time = 75.0
        Assert.assertEquals(testElevator.getCurrentPosition(), 4.0, DELTA_ALLOWED);
        Assert.assertEquals(testElevator.getState(), AbstractElevator.State.IDLE);
        Assert.assertEquals(testElevator.getActiveRequests().size(), 0);

        simulation.incrementTime(0.5);     // time = 75.5
        Assert.assertEquals(testElevator.getCurrentPosition(), 3.95, DELTA_ALLOWED);
        Assert.assertEquals(testElevator.getState(), AbstractElevator.State.DESCENDING);
        Assert.assertEquals(testElevator.getActiveRequests().size(), 0);

        simulation.incrementTime(0.5);     // time = 76.0
        Assert.assertEquals(testElevator.getCurrentPosition(), 3.9, DELTA_ALLOWED);
        Assert.assertEquals(testElevator.getState(), AbstractElevator.State.DESCENDING);
        Assert.assertEquals(testElevator.getActiveRequests().size(), 0);

        simulation.incrementTime(15.0);     // time = 91.0
        Assert.assertEquals(testElevator.getCurrentPosition(), 2.4, DELTA_ALLOWED);
        Assert.assertEquals(testElevator.getState(), AbstractElevator.State.DESCENDING);
        Assert.assertEquals(testElevator.getActiveRequests().size(), 0);

        //  Elevator should arrive for pickup at time=95.0

        simulation.incrementTime(5.0);     // time = 96.0
        Assert.assertEquals(testElevator.getCurrentPosition(), 2.0, DELTA_ALLOWED);
        Assert.assertEquals(testElevator.getState(), AbstractElevator.State.LOADING);
        Assert.assertEquals(testElevator.getActiveRequests().size(), 1);

        simulation.incrementTime(10.0);     // time = 106.0
        Assert.assertEquals(testElevator.getCurrentPosition(), 2.0, DELTA_ALLOWED);
        Assert.assertEquals(testElevator.getState(), AbstractElevator.State.LOADING);
        Assert.assertEquals(testElevator.getActiveRequests().size(), 1);

        //  Elevator should start descending at time=110.0
        
        simulation.incrementTime(10.0);     // time = 116.0
        Assert.assertEquals(testElevator.getCurrentPosition(), 1.4, DELTA_ALLOWED);
        Assert.assertEquals(testElevator.getState(), AbstractElevator.State.DESCENDING);
        Assert.assertEquals(testElevator.getActiveRequests().size(), 1);

        simulation.incrementTime(10.0);     // time = 126.0
        Assert.assertEquals(testElevator.getCurrentPosition(), 0.4, DELTA_ALLOWED);
        Assert.assertEquals(testElevator.getState(), AbstractElevator.State.DESCENDING);
        Assert.assertEquals(testElevator.getActiveRequests().size(), 1);

        simulation.incrementTime(10.0);     // time = 136.0
        Assert.assertEquals(testElevator.getCurrentPosition(), 0.0, DELTA_ALLOWED);
        Assert.assertEquals(testElevator.getState(), AbstractElevator.State.LOADING);
        Assert.assertEquals(testElevator.getActiveRequests().size(), 0);
        
        //  Elevator should arrive at ground floor at time=140.0
        simulation.incrementTime(10.0);     // time = 146.0
        Assert.assertEquals(testElevator.getCurrentPosition(), 0.0, DELTA_ALLOWED);
        Assert.assertEquals(testElevator.getState(), AbstractElevator.State.IDLE);
        Assert.assertEquals(testElevator.getActiveRequests().size(), 0);

        Assert.assertEquals(simulation.getState(), Simulation.State.FINISHED);
    }
}
