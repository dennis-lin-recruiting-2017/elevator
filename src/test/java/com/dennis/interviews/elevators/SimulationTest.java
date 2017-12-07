package com.dennis.interviews.elevators;

import java.util.LinkedList;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.dennis.interviews.elevators.elevator.OpportunisticElevator;

public class SimulationTest {
	public static final double DELTA_ALLOWED = 0.01;

	@Test
	public void testSingleAscendingElevatorPickupAndDropOff() {
		AbstractElevator testElevator = new OpportunisticElevator("test1");
		testElevator.setCurrentPosition(1.0);
		testElevator.setState(AbstractElevator.State.ASCENDING);
		List<AbstractElevator> listElevators = new LinkedList<>();
		listElevators.add(testElevator);

		List<PickupRequest> sortedRequests = new LinkedList<>();
		PickupRequest pickupRequest = new PickupRequest(2, 2, 1.0); // Start on 2nd floor, go up 2 floors, show up at t=100.
		sortedRequests.add(pickupRequest);

		Simulation simulation = new Simulation(listElevators, sortedRequests);
		simulation.incrementTime(9.0);
		Assert.assertEquals(testElevator.getCurrentPosition(), 1.9, DELTA_ALLOWED);
		Assert.assertEquals(testElevator.getActiveRequests().size(), 0);
		Assert.assertEquals(testElevator.getState(), AbstractElevator.State.ASCENDING);

		simulation.incrementTime(2.0);
		Assert.assertEquals(testElevator.getCurrentPosition(), 2.0, DELTA_ALLOWED);
		Assert.assertEquals(testElevator.getActiveRequests().size(), 1);
		Assert.assertEquals(testElevator.getServicedRequests().size(), 0);
		Assert.assertEquals(testElevator.getTargetFloor(), pickupRequest.getTargetFloor(), DELTA_ALLOWED);
		Assert.assertEquals(testElevator.getState(), AbstractElevator.State.LOADING);

		simulation.incrementTime(15.0);
		Assert.assertEquals(testElevator.getCurrentPosition(), 2.1, DELTA_ALLOWED);
		Assert.assertEquals(testElevator.getActiveRequests().size(), 1);
		Assert.assertEquals(testElevator.getServicedRequests().size(), 0);
		Assert.assertEquals(testElevator.getState(), AbstractElevator.State.ASCENDING);

		simulation.incrementTime(15.0);
		Assert.assertEquals(testElevator.getCurrentPosition(), 3.6, DELTA_ALLOWED);
		Assert.assertEquals(testElevator.getActiveRequests().size(), 1);
		Assert.assertEquals(testElevator.getServicedRequests().size(), 0);
		Assert.assertEquals(testElevator.getState(), AbstractElevator.State.ASCENDING);

		simulation.incrementTime(10.0);
		Assert.assertEquals(testElevator.getCurrentPosition(), 4.0, DELTA_ALLOWED);
		Assert.assertEquals(testElevator.getActiveRequests().size(), 0);
		Assert.assertEquals(testElevator.getServicedRequests().size(), 1);
		Assert.assertEquals(testElevator.getState(), AbstractElevator.State.LOADING);

		simulation.incrementTime(10.0);
		Assert.assertEquals(testElevator.getCurrentPosition(), 4.0, DELTA_ALLOWED);
		Assert.assertEquals(testElevator.getActiveRequests().size(), 0);
		Assert.assertEquals(testElevator.getServicedRequests().size(), 1);
		Assert.assertEquals(testElevator.getState(), AbstractElevator.State.IDLE);
	}

	@Test
	public void testSingleAscendingElevatorNoPickup() {
	    AbstractElevator testElevator = new OpportunisticElevator("test1");
	    testElevator.setCurrentPosition(1.0);
	    testElevator.setState(AbstractElevator.State.ASCENDING);
	    testElevator.setTargetFloor(5.0);
	    List<AbstractElevator> listElevators = new LinkedList<>();
	    listElevators.add(testElevator);

	    List<PickupRequest> sortedRequests = new LinkedList<>();
	    PickupRequest pickupRequest = new PickupRequest(2, 2, 100.0); // Start on 2nd floor, go up 2 floors, show up at t=100.
	    sortedRequests.add(pickupRequest);

	    Simulation simulation = new Simulation(listElevators, sortedRequests);
	    simulation.incrementTime(9.0);
	    Assert.assertEquals(testElevator.getCurrentPosition(), 1.9, DELTA_ALLOWED);
	    Assert.assertEquals(testElevator.getActiveRequests().size(), 0);
	    Assert.assertEquals(testElevator.getState(), AbstractElevator.State.ASCENDING);

	    simulation.incrementTime(2.0);
	    Assert.assertEquals(testElevator.getCurrentPosition(), 2.1, DELTA_ALLOWED);
	    Assert.assertEquals(testElevator.getActiveRequests().size(), 0);
	    Assert.assertEquals(testElevator.getServicedRequests().size(), 0);
	    Assert.assertEquals(testElevator.getTargetFloor(), 5.0, DELTA_ALLOWED);
	    Assert.assertEquals(testElevator.getState(), AbstractElevator.State.ASCENDING);
	}

	@Test
    public void testSingleDescendingElevatorPickupAndDropOff() {
        AbstractElevator testElevator = new OpportunisticElevator("test1");
        testElevator.setCurrentPosition(5.0);
        testElevator.setTargetFloor(1.0);
        List<AbstractElevator> listElevators = new LinkedList<>();
        listElevators.add(testElevator);

        List<PickupRequest> sortedRequests = new LinkedList<>();
        PickupRequest pickupRequest = new PickupRequest(4, -2, 1.0); // Start on 2nd floor, go up 2 floors, show up at t=100.
        sortedRequests.add(pickupRequest);

        Simulation simulation = new Simulation(listElevators, sortedRequests);
        simulation.incrementTime(9.0);
        Assert.assertEquals(testElevator.getCurrentPosition(), 4.1, DELTA_ALLOWED);
        Assert.assertEquals(testElevator.getActiveRequests().size(), 0);
        Assert.assertEquals(testElevator.getState(), AbstractElevator.State.DESCENDING);

        simulation.incrementTime(2.0);
        Assert.assertEquals(testElevator.getCurrentPosition(), 4.0, DELTA_ALLOWED);
        Assert.assertEquals(testElevator.getActiveRequests().size(), 1);
        Assert.assertEquals(testElevator.getServicedRequests().size(), 0);
        Assert.assertEquals(testElevator.getTargetFloor(), 1.0, DELTA_ALLOWED);
        Assert.assertEquals(testElevator.getState(), AbstractElevator.State.LOADING);

        simulation.incrementTime(15.0);
        Assert.assertEquals(testElevator.getCurrentPosition(), 3.9, DELTA_ALLOWED);
        Assert.assertEquals(testElevator.getActiveRequests().size(), 1);
        Assert.assertEquals(testElevator.getServicedRequests().size(), 0);
        Assert.assertEquals(testElevator.getState(), AbstractElevator.State.DESCENDING);

        simulation.incrementTime(15.0);
        Assert.assertEquals(testElevator.getCurrentPosition(), 2.4, DELTA_ALLOWED);
        Assert.assertEquals(testElevator.getActiveRequests().size(), 1);
        Assert.assertEquals(testElevator.getServicedRequests().size(), 0);
        Assert.assertEquals(testElevator.getState(), AbstractElevator.State.DESCENDING);

        simulation.incrementTime(10.0);
        Assert.assertEquals(testElevator.getCurrentPosition(), 2.0, DELTA_ALLOWED);
        Assert.assertEquals(testElevator.getActiveRequests().size(), 0);
        Assert.assertEquals(testElevator.getServicedRequests().size(), 1);
        Assert.assertEquals(testElevator.getState(), AbstractElevator.State.LOADING);

        simulation.incrementTime(10.0);
        Assert.assertEquals(testElevator.getCurrentPosition(), 1.9, DELTA_ALLOWED);
        Assert.assertEquals(testElevator.getActiveRequests().size(), 0);
        Assert.assertEquals(testElevator.getServicedRequests().size(), 1);
        Assert.assertEquals(testElevator.getState(), AbstractElevator.State.DESCENDING);
        
        simulation.incrementTime(10.0);
        Assert.assertEquals(testElevator.getCurrentPosition(), 1.0, DELTA_ALLOWED);
        Assert.assertEquals(testElevator.getActiveRequests().size(), 0);
        Assert.assertEquals(testElevator.getServicedRequests().size(), 1);
        Assert.assertEquals(testElevator.getState(), AbstractElevator.State.IDLE);
	}

	@Test
	public void testTwoDisparateElevators() {
		AbstractElevator testElevator1 = new OpportunisticElevator("test1");
		testElevator1.setCurrentPosition(1.0);
		testElevator1.setTargetFloor(3.0);

		AbstractElevator testElevator2 = new OpportunisticElevator("test2");
		testElevator2.setCurrentPosition(5.0);
		testElevator2.setTargetFloor(3.0);

		List<AbstractElevator> listElevators = new LinkedList<>();
		listElevators.add(testElevator1);
		listElevators.add(testElevator2);

		PickupRequest pickupRequest = new PickupRequest(2, 2, 1.0); // Start on 2nd floor, go up 2 floors, show up at t=100.
		List<PickupRequest> sortedRequests = new LinkedList<>();
		sortedRequests.add(pickupRequest);

		Simulation simulation = new Simulation(listElevators, sortedRequests);
		simulation.incrementTime(10.0);
		Assert.assertEquals(testElevator1.getCurrentPosition(), 2.0, DELTA_ALLOWED);
		Assert.assertEquals(testElevator2.getCurrentPosition(), 4.0, DELTA_ALLOWED);

		simulation.incrementTime(1.0);
		Assert.assertEquals(testElevator1.getCurrentPosition(), 2.0, DELTA_ALLOWED);
		Assert.assertEquals(testElevator2.getCurrentPosition(), 4.0, DELTA_ALLOWED);

		simulation.incrementTime(10.0);
		Assert.assertEquals(testElevator1.getCurrentPosition(), 2.0, DELTA_ALLOWED);
		Assert.assertEquals(testElevator2.getCurrentPosition(), 4.0, DELTA_ALLOWED);
	}

	@Test
	public void testOverloadedElevator() {
		AbstractElevator testElevator1 = new OpportunisticElevator("test1");
		testElevator1.setCurrentPosition(2.0);
		testElevator1.setTargetFloor(5.0);
		testElevator1.setMaxWeight(3);

		AbstractElevator testElevator2 = new OpportunisticElevator("test2");
		testElevator2.setCurrentPosition(1.0);
		testElevator2.setTargetFloor(5.0);
		testElevator2.setMaxWeight(3);

		List<AbstractElevator> listElevators = new LinkedList<>();
		listElevators.add(testElevator1);
		listElevators.add(testElevator2);

		List<PickupRequest> sortedRequests = new LinkedList<>();
		for (int counter = 0; counter < 5; counter++) {
			PickupRequest pickupRequest = new PickupRequest(3, 2, 0.0); // Start on 3rd floor, go up 2 floors, show up at t=100.
			sortedRequests.add(pickupRequest);
		}

		Simulation simulation = new Simulation(listElevators, sortedRequests);
		simulation.incrementTime(15.0);
		Assert.assertEquals(testElevator1.getCurrentPosition(), 3.0, DELTA_ALLOWED);
		Assert.assertEquals(testElevator2.getCurrentPosition(), 2.5, DELTA_ALLOWED);
	}
}
