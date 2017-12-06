package com.dennis.interviews.elevators.scheduler;

import java.util.List;

import com.dennis.interviews.elevators.AbstractElevator;
import com.dennis.interviews.elevators.PickupRequest;

public abstract class AbstractElevatorScheduler {
	public abstract AbstractElevator scheduleElevator(final List<AbstractElevator> listElevators, final PickupRequest nextPickupRequest);
}
