package com.example.dustcalc.input;

/**
 * A redstone wire block at a coordinate and its initial redstone power (0–15).
 * Users must explicitly specify the power level in the input.
 */
public record DustBlockSpec(int x, int y, int z, int power) {}