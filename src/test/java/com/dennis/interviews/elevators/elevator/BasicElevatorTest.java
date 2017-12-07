package com.dennis.interviews.elevators.elevator;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.dennis.interviews.elevators.AbstractElevator;

/**
 * Tests basic elevator movement and state changes.
 *
 * @author dennislin
 */
public class BasicElevatorTest extends AbstractElevatorTest {
    @Test
    public void testMovementAscendBetweenTwoPointsOverTime() {
        //  Initialize elevator
        AbstractElevator elevator = new OpportunisticElevator("test");
        elevator.setCurrentPosition(1.0);
        elevator.setMaxFloor(10.0);

        //  Make the elevator ascend with no interruptions
        elevator.setTargetFloor(10.0);
        elevator.incrementTime(1.0);
        Assert.assertEquals(elevator.getCurrentPosition(), 1.1, DELTA_ALLOWED);

        //  Make the elevator ascend with no interruptions
        elevator.incrementTime(1.0);
        Assert.assertEquals(elevator.getCurrentPosition(), 1.2, DELTA_ALLOWED);
    }

    @Test
    public void testMovementBetweenTwoPointsInTimeHittingTargetFloor() {
        AbstractElevator elevator = new OpportunisticElevator("test");
        elevator.setCurrentPosition(1.0);
        elevator.setMaxFloor(2.0);

        //  Make the elevator ascend and check right before the floor is reached
        elevator.setTargetFloor(2.0);
        elevator.incrementTime(9.5);
        Assert.assertEquals(elevator.getCurrentPosition(), 1.95, DELTA_ALLOWED);

        //  Make the elevator ascend and check right after the floor is reached
        elevator.incrementTime(1.0);
        Assert.assertEquals(elevator.getCurrentPosition(), 2.0, DELTA_ALLOWED);
        Assert.assertEquals(elevator.getState(), AbstractElevator.State.IDLE);

        //  Check that the elevator has finished and is now idle
        elevator.incrementTime(15.0);
        Assert.assertEquals(elevator.getState(), AbstractElevator.State.IDLE);
        Assert.assertEquals(elevator.getTimeInCurrentState(), 15.5, DELTA_ALLOWED);
    }

    @Test
    public void testMovementDescendBetweenTwoPointsOverTime() {
        //  Initialize elevator
        AbstractElevator elevator = new OpportunisticElevator("test");
        elevator.setCurrentPosition(5.0);
        elevator.setMaxFloor(10.0);

        //  Make the elevator descend with no interruptions
        elevator.setTargetFloor(4.0);
        elevator.incrementTime(1.0);
        Assert.assertEquals(elevator.getCurrentPosition(), 4.9, DELTA_ALLOWED);
        Assert.assertEquals(elevator.getTimeInCurrentState(), 1.0, DELTA_ALLOWED);

        //  Make the elevator descend with no interruptions
        elevator.incrementTime(1.0);
        Assert.assertEquals(elevator.getCurrentPosition(), 4.8, DELTA_ALLOWED);
        Assert.assertEquals(elevator.getTimeInCurrentState(), 2.0, DELTA_ALLOWED);
    }

    @Test
    public void testMovementDescendBetweenTwoPointsInTimeHittingTargetFloor() {
        AbstractElevator elevator = new OpportunisticElevator("test");
        elevator.setCurrentPosition(5.0);
        elevator.setMaxFloor(10.0);

        //  Make the elevator descend and check right before the floor is reached
        elevator.setTargetFloor(4.0);
        elevator.incrementTime(9.5);
        Assert.assertEquals(elevator.getCurrentPosition(), 4.05, DELTA_ALLOWED);
        Assert.assertEquals(elevator.getTimeInCurrentState(), 9.5, DELTA_ALLOWED);
        Assert.assertEquals(elevator.getState(), AbstractElevator.State.DESCENDING);

        //  Make the elevator descend and check right after the floor is reached
        elevator.incrementTime(1.0);
        Assert.assertEquals(elevator.getCurrentPosition(), 4.0, DELTA_ALLOWED);
        Assert.assertEquals(elevator.getState(), AbstractElevator.State.IDLE);
        Assert.assertEquals(elevator.getTimeInCurrentState(), 0.5, DELTA_ALLOWED);

        //  Check that the elevator has finished and is now idle
        elevator.incrementTime(15.0);
        Assert.assertEquals(elevator.getState(), AbstractElevator.State.IDLE);
        Assert.assertEquals(elevator.getTimeInCurrentState(), 15.5, DELTA_ALLOWED);
    }

    @DataProvider(name="getDataForTestDidCrossFloorAscending")
    public Object[][] getDataForTestDidCrossFloorAscending() {
        return new Object[][] {
            new Object[] { 1.0, 0.1, 9, false },
            new Object[] { 1.0, 0.1, 10, true },
            new Object[] { 1.0, 0.1, 11, true },
        };
    }

    @Test(dataProvider="getDataForTestDidCrossFloorAscending")
    public void testDidCrossFloorAscending(double startingPosition, double speed, double timeIncrement, boolean result) {
        Assert.assertEquals(AbstractElevator.didCrossFloorAscending(startingPosition, speed, timeIncrement), result);
    }

    @DataProvider(name="getDataForCalculateTimeElapsedCrossFloorAscending")
    public Object[][] getDataForCalculateTimeElapsedCrossFloorAscending() {
        return new Object[][] {
            new Object[] { 1.5, 0.1, 10.0, 5.0 },
            new Object[] { 1.0, 0.1, 11.0, 10.0 },
        };
    }

    @Test(dataProvider="getDataForCalculateTimeElapsedCrossFloorAscending")
    public void testCalculateTimeElapsedCrossFloorAscending(double startingPosition, double speed, double timeIncrement, double expectedTime) {
        Assert.assertEquals(AbstractElevator.calculateElapsedTimeWhenCrossingFloorsAscending(startingPosition, speed, timeIncrement), expectedTime, DELTA_ALLOWED);
    }

    @DataProvider(name="getDataForTestDidCrossFloorDescending")
    public Object[][] getDataForTestDidCrossFloorDescending() {
        return new Object[][] {
            new Object[] { 2.0, 0.1, 9, false },
            new Object[] { 2.0, 0.1, 10, true },
            new Object[] { 2.0, 0.1, 11, true },
        };
    }

    @Test(dataProvider="getDataForTestDidCrossFloorDescending")
    public void testDidCrossFloorDescending(double startingPosition, double speed, double timeIncrement, boolean result) {
        Assert.assertEquals(AbstractElevator.didCrossFloorDescending(startingPosition, speed, timeIncrement), result);
    }

    @DataProvider(name="getDataForCalculateTimeElapsedCrossFloorDescending")
    public Object[][] getDataForCalculateTimeElapsedCrossFloorDescending() {
        return new Object[][] {
            new Object[] { 2.5, 0.1, 10.0, 5.0 },
            new Object[] { 2.0, 0.1, 11.0, 10.0 },
        };
    }

    @Test(dataProvider="getDataForCalculateTimeElapsedCrossFloorDescending")
    public void testCalculateTimeElapsedCrossFloorDescending(double startingPosition, double speed, double timeIncrement, double expectedTime) {
        Assert.assertEquals(AbstractElevator.calculateElapsedTimeWhenCrossingFloorsAscending(startingPosition, speed, timeIncrement), expectedTime, DELTA_ALLOWED);
    }
};
