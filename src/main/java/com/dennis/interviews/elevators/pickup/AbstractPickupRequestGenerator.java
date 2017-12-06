package com.dennis.interviews.elevators.pickup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import com.dennis.interviews.elevators.PickupRequest;

public abstract class AbstractPickupRequestGenerator {
	protected AbstractPickupRequestGenerator() {
		// Do nothing.
	}
	
	protected abstract int generateRandomFloor();
	
	protected abstract double generateRandomTimestamp();
	
	public final List<PickupRequest> generatePickupRequests(int numberOfRequests) {
		ArrayList<PickupRequest> listPickupRequests = new ArrayList<>();
		for (int counter = 0; counter < numberOfRequests; counter++) {
			int startingFloor = generateRandomFloor();
			int endingFloor = generateRandomFloor();
			double arrivalTime = generateRandomTimestamp();
			
			while (endingFloor == startingFloor) {
				startingFloor = generateRandomFloor();
				endingFloor = generateRandomFloor();
			}
			
			PickupRequest pickupRequest = new PickupRequest(startingFloor, endingFloor - startingFloor, arrivalTime);
			listPickupRequests.add(pickupRequest);
			System.out.println(pickupRequest.toString());
		}
		
		PickupRequest[] sortedRequests = new PickupRequest[listPickupRequests.size()];
		listPickupRequests.toArray(sortedRequests);
		
		Comparator<PickupRequest> comparator = new Comparator<PickupRequest>() {
			@Override
			public int compare(PickupRequest a, PickupRequest b) {
				if (a.getTimestamp() > b.getTimestamp()) {
					return 1;
				} else if (a.getTimestamp() == b.getTimestamp()) {
					return 0;
				} else {
					return -1;
				}
			}
		};
		
		Arrays.sort(sortedRequests, comparator);
		List<PickupRequest> sortedList = new ArrayList<>();
		for (PickupRequest request : sortedRequests) {
			sortedList.add(request);
		}	
		
		return sortedList;
	}
}
