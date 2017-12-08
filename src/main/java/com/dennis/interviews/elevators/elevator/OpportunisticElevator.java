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
		for (PickupRequest pickupRequest : getActiveRequests()) {
			if (pickupRequest.getTargetFloor() == iNextFloor) {
				setRidersGettingOffOnNextFloor.add(pickupRequest);
			}
		}

		//  3.  Next, check to see if anybody wanted to get on the floor at the time.
		List<PickupRequest> listPickupRequestsAtFloor = null;
		if (null != getSimulation()) {
		    listPickupRequestsAtFloor = getSimulation().getMapActiveRequestsByFloor().get(iNextFloor);
		}

		Set<PickupRequest> newRiders = new HashSet<>();
		while ((null != listPickupRequestsAtFloor) && !listPickupRequestsAtFloor.isEmpty()) {
		    if (getMaxWeight() <= getActiveRequests().size() + newRiders.size() - setRidersGettingOffOnNextFloor.size()) {
		        break;
		    }
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

		if (setRidersGettingOffOnNextFloor.isEmpty() && newRiders.isEmpty()) {

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

		// ... and pick up the new ones who are waiting
		for (PickupRequest newRider : newRiders) {
		    setTargetFloor(Math.max(newRider.getTargetFloor(), getTargetFloor()));
		    addPickupRequest(newRider);
		}

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
        for (PickupRequest pickupRequest : getActiveRequests()) {
            if (pickupRequest.getTargetFloor() == iNextFloor) {
                setRidersGettingOffOnNextFloor.add(pickupRequest);
            }
        }

        //  3.  Next, check to see if anybody wanted to get on the floor at the time.
        List<PickupRequest> listPickupRequestsAtFloor = null;
        if (null != getSimulation()) {
            listPickupRequestsAtFloor = getSimulation().getMapActiveRequestsByFloor().get(iNextFloor);
        }
        Set<PickupRequest> newRiders = new HashSet<>();
        while ((null != listPickupRequestsAtFloor) && !listPickupRequestsAtFloor.isEmpty()) {
            if (getMaxWeight() <= getActiveRequests().size() - setRidersGettingOffOnNextFloor.size() + newRiders.size()) {
                break;
            }
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

        if (setRidersGettingOffOnNextFloor.isEmpty() && newRiders.isEmpty()) {

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

        // ... and pick up the new ones who are waiting
        for (PickupRequest newRider : newRiders) {
            setTargetFloor(Math.min(newRider.getTargetFloor(), getTargetFloor()));
            addPickupRequest(newRider);
        }

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
