package com.example.dustcalc.input;

/**
 * A block that blocks connectivity when placed atop a wire.
 */
public record BlockingBlock(int x, int y, int z, String blockType) {}