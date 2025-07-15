package com.example.dustcalc.model;

import com.example.dustcalc.input.PowerSourceSpec;
import java.util.*;

/**
 * Simulates signal propagation through dust blocks, tracing every update-call.
 * Each source direction is fully resolved before the next.
 * Uses push-trigger and pull-compute: when prompted, a dust pulls
 * its new power from all upstream powerTargets, and if it changes,
 * records the update and prompts its updateTargets.
 */
public class UpdateTracer {

    /**
     * Represents a single update from one node to another at a given power.
     */
    public static record TraceEntry(String from, String to, int power) {}

    // Order of source offsets: WEST, EAST, DOWN, UP, NORTH, SOUTH
    private static final int[][] SOURCE_OFFSETS = PowerSourceBlock.getSourceOffsets();

    private final List<TraceEntry> trace = new ArrayList<>();
    private final Map<String, DustBlock> lookup;
    private final int[] initialPowers;

    public UpdateTracer(List<DustBlock> graph) {
        this.lookup = new HashMap<>();
        int[] powers = new int[graph.size()];
        for (int i = 0; i < graph.size(); i++) {
            DustBlock b = graph.get(i);
            lookup.put(key(b.getX(), b.getY(), b.getZ()), b);
            powers[i] = b.getPower();
        }
        this.initialPowers = powers;
    }

    /**
     * Runs a full trace: processes each power-source direction in order,
     * then returns the list of all TraceEntry records.
     */
    public List<TraceEntry> traceAll(PowerSourceSpec src) {
        // Seed source power and trigger each masked direction
        for (int dir = 0; dir < SOURCE_OFFSETS.length; dir++) {
            if (!src.powerMask()[dir]) continue;
            int[] off = SOURCE_OFFSETS[dir];
            String coord = key(src.x() + off[0], src.y() + off[1], src.z() + off[2]);
            DustBlock target = lookup.get(coord);
            if (target != null) {
                // Trigger propagation
                dfsPropagate("SOURCE", target);
            }
        }
        return List.copyOf(trace);
    }

    /**
     * Push-trigger, pull-compute propagation:
     * 1) Pull max upstream power minus one
     * 2) If changed, record and update
     * 3) Prompt all downstream updateTargets
     */
    private void dfsPropagate(String fromId, DustBlock curr) {
        // Compute new power from all upstream sources
        int newPower = curr.getPowerTargets().stream()
                .mapToInt(up -> up.getPower())
                .max().orElse(0) - 1;
        if (newPower < 0) newPower = 0;
        // Only proceed if power changed
        if (newPower == curr.getPower()) {
            return;
        }
        // Record update
        String currId = key(curr.getX(), curr.getY(), curr.getZ());
        trace.add(new TraceEntry(fromId, currId, newPower));
        curr.setPower(newPower);
        // Push triggers to all downstream neighbors
        for (DustBlock nb : curr.getUpdateTargets()) {
            dfsPropagate(currId, nb);
        }
    }

    private static String key(int x, int y, int z) {
        return x + ":" + y + ":" + z;
    }

    /**
     * Returns an immutable copy of the trace log.
     */
    public List<TraceEntry> getTrace() {
        return List.copyOf(trace);
    }

    /**
     * Returns an array list of initial powers.
     */
    public int[] getInitialPowers() {
        return initialPowers;
    }
}