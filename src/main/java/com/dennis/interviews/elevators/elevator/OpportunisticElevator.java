package com.dennis.interviews.elevators.elevator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dennis.interviews.elevators.AbstractElevator;

public class OpportunisticElevator extends AbstractElevator {
    private static final Logger LOG = LoggerFactory.getLogger(OpportunisticElevator.class);
    public OpportunisticElevator(final String name) {
        super(name);
    }

    public void processStateAscending(double timeIncrement) {
        final double distanceToTravel = timeIncrement * getSpeed();
        final double newPosition = getCurrentPosition() + distanceToTravel;
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
        openElevatorDoors(newPosition, nextFloor, timeIncrement, timeElapsedUntilCrossingFloors);
    }


    public void processStateDescending(double timeIncrement) {
        final double distanceToTravel = timeIncrement * getSpeed();
        final double newPosition = getCurrentPosition() - distanceToTravel;
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
        openElevatorDoors(newPosition, nextFloor, timeIncrement, timeElapsedUntilCrossingFloors);
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
}
