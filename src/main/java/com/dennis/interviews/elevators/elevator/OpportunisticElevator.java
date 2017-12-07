package com.dennis.interviews.elevators.elevator;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dennis.interviews.elevators.AbstractElevator;
import com.dennis.interviews.elevators.PickupRequest;

public class OpportunisticElevator extends AbstractElevator {
	private static final Logger LOG = LoggerFactory.getLogger(OpportunisticElevator.class);
	public OpportunisticElevator(final String name) {
		super(name);
    }

	public void processStateAscending(double timeIncrement) {
		final double distanceToTravel = timeIncrement * getSpeed();
		final double newPosition = getCurrentPosition() + distanceToTravel;
		final double oldFloor = Math.floor(getCurrentPosition());
		final double nextFloor = Math.floor(newPosition);

		//  Never crossed a floor -- no special handling needed
		if (!AbstractElevator.didCrossFloorAscending(getCurrentPosition(), getSpeed(), timeIncrement)) {
			incrementTimeInCurrentState(timeIncrement);
			setCurrentPosition(newPosition);

			return;
		}

		//  We just crossed the a new floor!
		//  1.  First calculate the time we crossed the floor and increment the timestamp of the elevator.
		final double timeElapsedUntilCrossingFloors =
		        AbstractElevator.calculateElapsedTimeWhenCrossingFloorsAscending(getCurrentPosition(), getSpeed(), timeIncrement);
		incrementTimeInCurrentState(timeElapsedUntilCrossingFloors);
		final double timeInNewState = timeIncrement - timeElapsedUntilCrossingFloors;
		final double timestampCrossingFloors = getCurrentTimestamp() + timeElapsedUntilCrossingFloors;
		setCurrentPosition(nextFloor);

		//  2.  First check to see if anybody is getting off on the new floor.
		Set<PickupRequest> setRidersGettingOffOnNextFloor = new HashSet<>();
		final int iNextFloor = (int) nextFloor;
		System.out.println("Old floor: " + oldFloor);
		System.out.println("New floor 1: " + nextFloor);
		System.out.println("New floor 2: " + iNextFloor);
		System.out.println("*** BEFORE - active: " + getActiveRequests().size());
		System.out.println("*** BEFORE - leaving : " + setRidersGettingOffOnNextFloor.size());
		for (PickupRequest pickupRequest : getActiveRequests()) {
			System.out.println("*** BEFORE - leaving - target floor: " + pickupRequest.getTargetFloor());
			if (pickupRequest.getTargetFloor() == iNextFloor) {
				setRidersGettingOffOnNextFloor.add(pickupRequest);
			}
		}
		System.out.println("*** AFTER - active: " + getActiveRequests().size());
		System.out.println("*** AFTER - leaving : " + setRidersGettingOffOnNextFloor.size());

		//  3.  Next, check to see if anybody wanted to get on the floor at the time.
		List<PickupRequest> listPickupRequestsAtFloor = getSimulation().getMapActiveRequestsByFloor().get(iNextFloor);
		Set<PickupRequest> newRiders = new HashSet<>();
		while ((null != listPickupRequestsAtFloor) && !listPickupRequestsAtFloor.isEmpty()) {
		    PickupRequest pickupRequest = listPickupRequestsAtFloor.get(0);
		    if (pickupRequest.getTimestamp() < timestampCrossingFloors) {
		        pickupRequest = listPickupRequestsAtFloor.remove(0);
		        //  Pickup time is when the elevator doors has closed.
		        pickupRequest.setTimestampPickup(timestampCrossingFloors + getPickupTimeRequired());
		        newRiders.add(pickupRequest);
		    } else {
		        break;
		    }
		}
		System.out.println("*** New riders: " + newRiders.size());

		if (setRidersGettingOffOnNextFloor.isEmpty() && newRiders.isEmpty()) {
		    System.out.println("**** No one getting on or off, continuing...");

		    if (getTargetFloor() > nextFloor) {
                setCurrentPosition(newPosition);
                incrementTimeInCurrentState(timeIncrement);
            } else {
                incrementTimeInCurrentState(timeElapsedUntilCrossingFloors);

                setCurrentPosition(nextFloor);
                setState(State.IDLE);
                incrementTimeInCurrentState(timeIncrement - timeElapsedUntilCrossingFloors);
            }
			return;
		}

		//  At the time we cross the floor, we want to drop off all the riders who are getting off...
		incrementTimeInCurrentState(timeElapsedUntilCrossingFloors);
		for (PickupRequest pickupRequest : setRidersGettingOffOnNextFloor) {
			pickupRequest.setTimestampDropoff(timestampCrossingFloors);
			getServicedRequests().add(pickupRequest);
			getActiveRequests().remove(pickupRequest);
		}
		System.out.println("*** AFTER 2 - active: " + getActiveRequests().size());
		System.out.println("*** AFTER 2 - leaving 1: " + timeElapsedUntilCrossingFloors);
		System.out.println("*** AFTER 2 - leaving 2: " + timeInNewState);
		System.out.println("*** AFTER 2 - leaving 3: " + setRidersGettingOffOnNextFloor.size());

		// ... and pick up the new ones who are waiting
		for (PickupRequest newRider : newRiders) {
		    setTargetFloor(Math.max(newRider.getTargetFloor(), getTargetFloor()));
		    addPickupRequest(newRider);
		}
		/*
		List<PickupRequest> queuedRiders = (null != getSimulation()) ?
				getSimulation().getMapActiveRequestsByFloor().get(new Integer(iNextFloor)) : null;
		while ((getActiveRequests().size() < getMaxWeight()) && (null != queuedRiders) && !queuedRiders.isEmpty()) {
			PickupRequest nextRider = queuedRiders.remove(0);
			nextRider.setTimestampPickup(getCurrentTimestamp());
			getActiveRequests().add(nextRider);
		}
		//*/

		//  Then we increment the loading times
		setState(State.LOADING);
		incrementTimeInCurrentState(timeInNewState);
	}

	public void processStateDescending(double timeIncrement) {
	    final double distanceToTravel = timeIncrement * getSpeed();
        final double newPosition = getCurrentPosition() - distanceToTravel;
        final double oldFloor = Math.ceil(getCurrentPosition());
        final double nextFloor = Math.ceil(newPosition);

        //  Never crossed a floor -- no special handling needed
        if (!AbstractElevator.didCrossFloorDescending(getCurrentPosition(), getSpeed(), timeIncrement)) {
            incrementTimeInCurrentState(timeIncrement);
            setCurrentPosition(newPosition);

            return;
        }

        //  We just crossed the a new floor!
        //  1.  First calculate the time we crossed the floor and increment the timestamp of the elevator.
        final double timeElapsedUntilCrossingFloors =
                AbstractElevator.calculateElapsedTimeWhenCrossingFloorsDescending(getCurrentPosition(), getSpeed(), timeIncrement);
        incrementTimeInCurrentState(timeElapsedUntilCrossingFloors);
        final double timeInNewState = timeIncrement - timeElapsedUntilCrossingFloors;
        final double timestampCrossingFloors = getCurrentTimestamp() + timeElapsedUntilCrossingFloors;
        setCurrentPosition(nextFloor);

        //  2.  First check to see if anybody is getting off on the new floor.
        Set<PickupRequest> setRidersGettingOffOnNextFloor = new HashSet<>();
        final int iNextFloor = (int) nextFloor;
        System.out.println("Old floor: " + oldFloor);
        System.out.println("New floor 1: " + nextFloor);
        System.out.println("New floor 2: " + iNextFloor);
        System.out.println("*** BEFORE - active: " + getActiveRequests().size());
        System.out.println("*** BEFORE - leaving : " + setRidersGettingOffOnNextFloor.size());
        for (PickupRequest pickupRequest : getActiveRequests()) {
            System.out.println("*** BEFORE - leaving - target floor: " + pickupRequest.getTargetFloor());
            if (pickupRequest.getTargetFloor() == iNextFloor) {
                setRidersGettingOffOnNextFloor.add(pickupRequest);
            }
        }
        System.out.println("*** AFTER - active: " + getActiveRequests().size());
        System.out.println("*** AFTER - leaving : " + setRidersGettingOffOnNextFloor.size());

        //  3.  Next, check to see if anybody wanted to get on the floor at the time.
        List<PickupRequest> listPickupRequestsAtFloor = getSimulation().getMapActiveRequestsByFloor().get(iNextFloor);
        Set<PickupRequest> newRiders = new HashSet<>();
        while ((null != listPickupRequestsAtFloor) && !listPickupRequestsAtFloor.isEmpty()) {
            PickupRequest pickupRequest = listPickupRequestsAtFloor.get(0);
            if (pickupRequest.getTimestamp() < timestampCrossingFloors) {
                pickupRequest = listPickupRequestsAtFloor.remove(0);
                //  Pickup time is when the elevator doors has closed.
                pickupRequest.setTimestampPickup(timestampCrossingFloors + getPickupTimeRequired());
                newRiders.add(pickupRequest);
            } else {
                break;
            }
        }
        System.out.println("*** New riders: " + newRiders.size());

        if (setRidersGettingOffOnNextFloor.isEmpty() && newRiders.isEmpty()) {
            System.out.println("**** No one getting on or off, continuing...");

            if (getTargetFloor() < nextFloor) {
                setCurrentPosition(newPosition);
                incrementTimeInCurrentState(timeIncrement);
            } else {
                incrementTimeInCurrentState(timeElapsedUntilCrossingFloors);

                setCurrentPosition(nextFloor);
                setState(State.IDLE);
                incrementTimeInCurrentState(timeIncrement - timeElapsedUntilCrossingFloors);
            }

            return;
        }

        //  At the time we cross the floor, we want to drop off all the riders who are getting off...
        incrementTimeInCurrentState(timeElapsedUntilCrossingFloors);
        for (PickupRequest pickupRequest : setRidersGettingOffOnNextFloor) {
            pickupRequest.setTimestampDropoff(timestampCrossingFloors);
            getServicedRequests().add(pickupRequest);
            getActiveRequests().remove(pickupRequest);
        }
        System.out.println("*** AFTER 2 - active: " + getActiveRequests().size());
        System.out.println("*** AFTER 2 - leaving 1: " + timeElapsedUntilCrossingFloors);
        System.out.println("*** AFTER 2 - leaving 2: " + timeInNewState);
        System.out.println("*** AFTER 2 - leaving 3: " + setRidersGettingOffOnNextFloor.size());

        // ... and pick up the new ones who are waiting
        for (PickupRequest newRider : newRiders) {
            setTargetFloor(Math.min(newRider.getTargetFloor(), getTargetFloor()));
            addPickupRequest(newRider);
        }
        /*
        List<PickupRequest> queuedRiders = (null != getSimulation()) ?
                getSimulation().getMapActiveRequestsByFloor().get(new Integer(iNextFloor)) : null;
        while ((getActiveRequests().size() < getMaxWeight()) && (null != queuedRiders) && !queuedRiders.isEmpty()) {
            PickupRequest nextRider = queuedRiders.remove(0);
            nextRider.setTimestampPickup(getCurrentTimestamp());
            getActiveRequests().add(nextRider);
        }
        //*/

        //  Then we increment the loading times
        setState(State.LOADING);
        incrementTimeInCurrentState(timeInNewState);
	}

	public void processStateLoading(double timeIncrement) {
		double newTimeInCurrentState = getTimeInCurrentState() + timeIncrement;

		if (newTimeInCurrentState < getPickupTimeRequired()) {
			incrementTimeInCurrentState(timeIncrement);
			return;
		}

		double timeAfterFinishedLoading = newTimeInCurrentState - getPickupTimeRequired();
		if (getTargetFloor() > getCurrentPosition()) {
			setState(State.ASCENDING);
		} else if (getTargetFloor() < getCurrentPosition()) {
			setState(State.DESCENDING);
		} else {
			setState(State.IDLE);
		}

		incrementTime(timeAfterFinishedLoading);
	}

	public void processStateIdle(double timeIncrement) {
		incrementTimeInCurrentState(timeIncrement);
	}

	@Override
    public void incrementTime(final double timeIncrement) {
		LOG.info("Elevator({})::increment() ; - processing '{}' state at timeInCurrentState={}, with timeIncrement={}",
				new Object[] {getName(), getState(), getTimeInCurrentState(), timeIncrement });
		/**
		 * I hate switch statements -- easy to forget the terminating break statement.
		 */
		if (State.ASCENDING == getState()) {
			processStateAscending(timeIncrement);
		} else if (State.DESCENDING == getState()) {
			processStateDescending(timeIncrement);
		} else if (State.LOADING == getState()) {
			processStateLoading(timeIncrement);
		} else if (State.IDLE == getState()) {
			processStateIdle(timeIncrement);
		} else {
			throw new IllegalArgumentException("Elevator is in invalid or unhandled state: " + getState());
		}
	}
}
