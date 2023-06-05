package com.raxim.myscoutee.profile.data.dto.internal;

public class Bound {
    private final int minGroupSize;
    private final int maxGroupSize;

    public Bound(int minGroupSize, int maxGroupSize) {
        this.minGroupSize = minGroupSize;
        this.maxGroupSize = maxGroupSize;
    }

    public int getMinGroupSize() {
        return minGroupSize;
    }

    public int getMaxGroupSize() {
        return maxGroupSize;
    }
}
