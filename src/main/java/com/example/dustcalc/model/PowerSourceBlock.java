package com.example.dustcalc.model;

import com.example.dustcalc.input.PowerSourceSpec;

/**
 * A fixed redstone power source for the circuit.
 * Holds an immutable power level and a cardinal-direction mask.
 */
public class PowerSourceBlock extends PowerBlock {
    private final boolean[] powerMask;  // [WEST, EAST, DOWN, UP, NORTH, SOUTH]
    private static final int[][] SOURCE_OFFSETS = {
            {-1,  0,  0}, { 1,  0,  0},
            { 0, -1,  0}, { 0,  1,  0},
            { 0,  0, -1}, { 0,  0,  1}
    };

    /**
     * Constructs a source at the spec's coordinates with its final power and mask.
     */
    public PowerSourceBlock(PowerSourceSpec spec) {
        super(spec.x(), spec.y(), spec.z(), spec.finalPower());
        this.powerMask = spec.powerMask();
    }

    /**
     * @return six-element mask of which directions this source powers
     */
    public boolean[] getPowerMask() {
        return powerMask;
    }

    /**
     * @return the global cardinal offsets (WEST, EAST, DOWN, UP, NORTH, SOUTH)
     */
    public static int[][] getSourceOffsets() {
        return SOURCE_OFFSETS;
    }
}