package com.example.dustcalc.input;

/**
 * Describes a single redstone power source within the circuit.
 *
 * @param x            X-coordinate of the source
 * @param y            Y-coordinate of the source
 * @param z            Z-coordinate of the source
 * @param finalPower   Final power level (0-15)
 * @param powerMask    Six-element mask of which cardinal directions to power:
 *                     [WEST, EAST, DOWN, UP, NORTH, SOUTH]
 */
public record PowerSourceSpec(
        int x,
        int y,
        int z,
        int finalPower,
        boolean[] powerMask
) {
    /**
     * Validates that the powerMask has exactly six entries.
     */
    public PowerSourceSpec {
        if (powerMask == null || powerMask.length != 6) {
            throw new IllegalArgumentException(
                    "powerMask must be non-null and length 6: [WEST, EAST, DOWN, UP, NORTH, SOUTH]"
            );
        }
    }
}