package com.dennis.interviews.elevators;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dennis.interviews.elevators.elevator.OpportunisticElevator;
import com.dennis.interviews.elevators.pickup.AbstractPickupRequestGenerator;
import com.dennis.interviews.elevators.pickup.RegularIntervalGenerator;

public class Simulation {
	public static enum State {
		NOT_STARTED,
		RUNNING,
		FINISHED;
	};
	private static final Logger LOG = LoggerFactory.getLogger(Simulation.class);
	
	private final List<AbstractElevator> listElevators = new ArrayList<>();
	private final List<PickupRequest> listPickupRequests;
	private final Map<Integer, List<PickupRequest>> mapActiveRequestsByFloor = new HashMap<>();

	private State state = State.NOT_STARTED;
	private static final double TIMESTAMP_INCREMENT = 1.0;
	private double currentTimestamp = 0.0;
	
	private Simulation() {
		throw new RuntimeException("Should not call default constructor.");
	}
	
	public Simulation(final List<AbstractElevator> elevators, final List<PickupRequest> pickupRequests) {
		for (AbstractElevator elevator : elevators) {
			elevator.setSimulation(this);
			listElevators.addAll(elevators);
		}
		
		listPickupRequests = pickupRequests;
	}
	
	public Simulation(final int numElevators, final List<PickupRequest> pickupRequests) {
		for (int counter = 0; counter < numElevators; counter++) {
			listElevators.add(new OpportunisticElevator(String.format("Elevator-%d", counter + 1)));
		}
		
		listPickupRequests = pickupRequests;
	}
	
	public final void simulate() {
		if (currentTimestamp > 0.0) {
			throw new IllegalStateException("Simulation has already been started -- can not restart.");
		}
	}
	
	public final State getState() {
		return state;
	}
	
	public final void incrementTime2(final double timeIncrement) {
		
	}
	
	public final void incrementTime(final double timeIncrement) {
		if (State.FINISHED == state) {
			throw new IllegalStateException("Simulation has already finished!");
		}
		
		double newTimestamp = currentTimestamp + timeIncrement;
		
		//  Make all the pickup requests between "currentTimestamp" and "newTimestamp" active.
		for (int counter = 0; counter < listPickupRequests.size(); counter++) {
			PickupRequest pickupRequest = listPickupRequests.get(counter);
			if (pickupRequest.getTimestamp() < newTimestamp) {
				if (null == mapActiveRequestsByFloor.get(pickupRequest.getStartingFloor())) {
					mapActiveRequestsByFloor.put(pickupRequest.getStartingFloor(), new LinkedList<PickupRequest>());
				}
				mapActiveRequestsByFloor.get(pickupRequest.getStartingFloor()).add(pickupRequest);
			} else {
				//  Since the list of requests is in chronological order, there is no need to process the rest of the
				//  requests if this current request is past the newTimestamp.
				break;
			}
		}
	
		//  If no more pickup requests to process, then continue to increment the time on the elevators.
		if (listPickupRequests.isEmpty()) {
			boolean allElevatorsIdle = true;
			for (AbstractElevator elevator : listElevators) {
				elevator.incrementTime(timeIncrement);
				allElevatorsIdle = allElevatorsIdle && (elevator.getState() == AbstractElevator.State.IDLE);
			}
			
			currentTimestamp = newTimestamp;
			if (allElevatorsIdle) {
				state = State.FINISHED;
			}
			
			return;
		}
		
		//  Now we need to schedule the pickup request.  First check to see if there are new pickup requests between now
		//  and the next pickup request.  If not, we can safely increment the time.
		if (newTimestamp < listPickupRequests.get(0).getTimestamp()) {
			for (AbstractElevator elevator : listElevators) {
				elevator.incrementTime(timeIncrement);
			}
			
			currentTimestamp = newTimestamp;
			
			return;
		}
	
		//  In order to safely schedule the next request, let's do the following:
		//  1.  increment the time until the next pickup request
		//  2.  schedule the pickup request
		//  3.  increment the remainder of the time.
		PickupRequest nextPickupRequest = listPickupRequests.remove(0);
		double incrementBefore = nextPickupRequest.getTimestamp() - currentTimestamp;
		double incrementAfter = timeIncrement - incrementBefore;
		
		//  1.  increment the time until the next pickup request
		for (AbstractElevator elevator : listElevators) {
		    elevator.incrementTime(incrementBefore);
		}
		
		//  2.  schedule the pickup request
		//      Need to handle the case when an elevator is busy
		schedulePickupRequest(nextPickupRequest);
		
		//  3.  increment the remainder of the time.
		for (AbstractElevator elevator : listElevators) {
		    elevator.incrementTime(incrementAfter);
		}
	}
	
	public final Map<Integer, List<PickupRequest>> getMapActiveRequestsByFloor() {
		return mapActiveRequestsByFloor;
	}
	
	public void schedulePickupRequest(final PickupRequest nextPickupRequest) {
		
	}
	
	public static final void main(final String[] args) {
		final double experimentDurationInSeconds = 20000.0;
		final AbstractPickupRequestGenerator pickupRequestGenerator = new RegularIntervalGenerator(0, 1, experimentDurationInSeconds);
		List<PickupRequest> sortedRequests = pickupRequestGenerator.generatePickupRequests(20);
		
		Simulation simulation = new Simulation(1, sortedRequests);
		simulation.simulate();
	}
}
