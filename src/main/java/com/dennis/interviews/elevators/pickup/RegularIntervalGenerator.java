package com.dennis.interviews.elevators.pickup;

import java.util.Random;

public class RegularIntervalGenerator extends AbstractPickupRequestGenerator {
    private static final Random RANDOM = new Random();
    private final int minFloor;
    private final int maxFloor;
    private final double experimentDurationInSeconds;

    public RegularIntervalGenerator(final int minFloor, final int maxFloor, final double experimentDurationInSeconds) {
        this.minFloor = minFloor;
        this.maxFloor = maxFloor;
        this.experimentDurationInSeconds = experimentDurationInSeconds;
    }

    @Override
    protected int generateRandomFloor() {
        return RANDOM.nextInt(maxFloor - minFloor + 1);
    }

    @Override
    protected double generateRandomTimestamp() {
        return RANDOM.nextDouble() * experimentDurationInSeconds;
    }
}
