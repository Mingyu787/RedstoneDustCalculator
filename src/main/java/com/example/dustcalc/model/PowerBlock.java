package com.example.dustcalc.model;

/**
 * Base class for any redstone power source or dust block.
 * Holds position coordinates and current power level.
 */
public abstract class PowerBlock {
    protected final int x;
    protected final int y;
    protected final int z;
    protected int power;

    /**
     * @param x            X-coordinate
     * @param y            Y-coordinate
     * @param z            Z-coordinate
     * @param power        Power level (0–15)
     */
    public PowerBlock(int x, int y, int z, int power) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.power = power;
    }

    // Position getters
    public int getX() { return x; }
    public int getY() { return y; }
    public int getZ() { return z; }

    // Power getters/setters
    public int getPower() { return power; }
    public void setPower(int power) { this.power = power; }
}