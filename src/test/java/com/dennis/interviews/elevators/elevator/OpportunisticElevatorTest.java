package com.dennis.interviews.elevators.elevator;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.dennis.interviews.elevators.AbstractElevator;
import com.dennis.interviews.elevators.PickupRequest;

public class OpportunisticElevatorTest extends AbstractElevatorTest {
	@Test
	public void testMovementAscendBetweenTwoPointsOverTime() {
		//  Initialize pickup request
		PickupRequest pickupRequest = new PickupRequest(1, 1, 0.0);
		
		//  Initialize elevator
		AbstractElevator elevator = new OpportunisticElevator("test");
		elevator.setCurrentPosition(1.0);
		elevator.setMaxFloor(10.0);
		//elevator.addPickupRequest(pickupRequest);
		elevator.getActiveRequests().add(pickupRequest);
	
		//  Make the elevator ascend with no interruptions
		elevator.setTargetFloor(10.0);
		elevator.incrementTime(1.0);
		Assert.assertEquals(elevator.getCurrentPosition(), 1.1, DELTA_ALLOWED);
		Assert.assertEquals(elevator.getState(), AbstractElevator.State.ASCENDING);
		
		//  Make the elevator ascend with no interruptions
		elevator.incrementTime(1.0);
		Assert.assertEquals(elevator.getCurrentPosition(), 1.2, DELTA_ALLOWED);
		Assert.assertEquals(elevator.getState(), AbstractElevator.State.ASCENDING);
		
		//  Make the elevator ascend to the floor of the pickup request and process the request
		elevator.incrementTime(9.0);
		Assert.assertEquals(elevator.getCurrentPosition(), 2.0, DELTA_ALLOWED);
		Assert.assertEquals(elevator.getState(), AbstractElevator.State.LOADING);
		
		//  Make the elevator ascend to the floor of the pickup request and process the request
		elevator.incrementTime(20.0);
		Assert.assertEquals(elevator.getCurrentPosition(), 2.6, DELTA_ALLOWED);
		Assert.assertEquals(elevator.getState(), AbstractElevator.State.ASCENDING);
	}
	
	@Test
	public void testMovementDesscendBetweenTwoPointsOverTime() {
		//  Initialize pickup request
		PickupRequest pickupRequest = new PickupRequest(6, -1, 0.0);
		
		//  Initialize elevator
		AbstractElevator elevator = new OpportunisticElevator("test");
		elevator.setCurrentPosition(6.0);
		elevator.setMaxFloor(10.0);
		//elevator.addPickupRequest(pickupRequest);
		elevator.getActiveRequests().add(pickupRequest);
	
		//  Make the elevator ascend with no interruptions
		elevator.setTargetFloor(1.0);
		elevator.incrementTime(1.0);
		Assert.assertEquals(elevator.getCurrentPosition(), 5.9, DELTA_ALLOWED);
		Assert.assertEquals(elevator.getState(), AbstractElevator.State.DESCENDING);
		
		//  Make the elevator ascend with no interruptions
		elevator.incrementTime(1.0);
		Assert.assertEquals(elevator.getCurrentPosition(), 5.8, DELTA_ALLOWED);
		Assert.assertEquals(elevator.getState(), AbstractElevator.State.DESCENDING);
		
		//  Make the elevator ascend to the floor of the pickup request and process the request
		elevator.incrementTime(9.0);
		Assert.assertEquals(elevator.getCurrentPosition(), 5.0, DELTA_ALLOWED);
		Assert.assertEquals(elevator.getState(), AbstractElevator.State.LOADING);
		
		//  Make the elevator ascend to the floor of the pickup request and process the request
		elevator.incrementTime(20.0);
		Assert.assertEquals(elevator.getCurrentPosition(), 4.4, DELTA_ALLOWED);
		Assert.assertEquals(elevator.getState(), AbstractElevator.State.DESCENDING);
	}

}
