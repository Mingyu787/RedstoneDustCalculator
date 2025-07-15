package com.example.dustcalc.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a redstone dust node in the simulation.
 * Extends PowerBlock to inherit position and mutable power.
 * Maintains:
 *  - powerTargets: upstream PowerBlocks (dusts or the source)
 *  - updateTargets: downstream DustBlocks to prompt updates
 */
public class DustBlock extends PowerBlock {
    private final List<PowerBlock> powerTargets = new ArrayList<>();
    private final List<DustBlock> updateTargets = new ArrayList<>();

    /**
     * @param x      X-coordinate
     * @param y      Y-coordinate
     * @param z      Z-coordinate
     * @param power  Initial power level (0–15)
     */
    public DustBlock(int x, int y, int z, int power) {
        super(x, y, z, power);
    }

    /**
     * @return an immutable list of upstream sources (dusts or power source)
     */
    public List<PowerBlock> getPowerTargets() {
        return Collections.unmodifiableList(powerTargets);
    }

    /**
     * Adds a PowerBlock (another dust or the source) that feeds into this dust.
     */
    public void addPowerTarget(PowerBlock upstream) {
        powerTargets.add(upstream);
    }

    /**
     * Clears all upstream power sources.
     */
    public void clearPowerTargets() {
        powerTargets.clear();
    }

    /**
     * @return an immutable list of downstream DustBlocks to prompt when this dust changes
     */
    public List<DustBlock> getUpdateTargets() {
        return Collections.unmodifiableList(updateTargets);
    }

    /**
     * Adds a DustBlock to prompt on power change.
     */
    public void addUpdateTarget(DustBlock downstream) {
        updateTargets.add(downstream);
    }

    /**
     * Clears all downstream update targets.
     */
    public void clearUpdateTargets() {
        updateTargets.clear();
    }
}