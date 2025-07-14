package com.example.dustcalc.input;

/**
 * A redstone power source coordinate and its initial redstone power (0–15).
 * Users must explicitly specify the power level in the input.
 */
public record PowerSource(int x, int y, int z, int power) {}