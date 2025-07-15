package com.example.dustcalc.model;

import com.example.dustcalc.input.Instance;
import com.example.dustcalc.input.DustBlockSpec;
import com.example.dustcalc.input.BlockingBlockSpec;
import com.example.dustcalc.input.TransparentBlockSpec;

import java.util.*;

/**
 * Builds both dust and source power blocks, wiring upstream connectivity.
 */
public class CircuitGraphBuilder {
    // Offsets for dust-dust connectivity: horizontal + staircase
    private static final int[][] DUST_OFFSETS = {
            { 1,  0,  0}, {-1,  0,  0},
            { 0,  0,  1}, { 0,  0, -1},
            { 1,  1,  0}, { 1, -1,  0},
            {-1,  1,  0}, {-1, -1,  0},
            { 0,  1,  1}, { 0,  1, -1},
            { 0, -1,  1}, { 0, -1, -1}
    };

    // Cardinal offsets for source connectivity: WEST, EAST, DOWN, UP, NORTH, SOUTH
    private static final int[][] SOURCE_OFFSETS = PowerSourceBlock.getSourceOffsets();


    /**
     * Constructs DustBlock nodes and a single PowerSourceBlock, wiring upstream powerTargets.
     */
    public List<DustBlock> build(Instance instance) {
        // 1) Instantiate PowerSourceBlock
        PowerSourceBlock source = new PowerSourceBlock(instance.powerSource());

        // 2) Instantiate DustBlock for each spec
        Map<String, DustBlock> map = new HashMap<>();
        for (DustBlockSpec spec : instance.dustBlocks()) {
            DustBlock db = new DustBlock(spec.x(), spec.y(), spec.z(), spec.power());
            map.put(key(spec.x(), spec.y(), spec.z()), db);
        }

        // 3) Wire dust-dust powerTargets
        for (DustBlock a : map.values()) {
            for (int[] off : DUST_OFFSETS) {
                String k = key(a.getX() + off[0], a.getY() + off[1], a.getZ() + off[2]);
                DustBlock b = map.get(k);
                if (b != null && isAffectedBy(a, b, instance.blockingBlocks(), instance.transparentBlocks())) {
                    a.addPowerTarget(b);
                }
            }
        }

        // 4) Wire source-dust powerTargets based on mask
        boolean[] mask = instance.powerSource().powerMask();
        for (int dir = 0; dir < SOURCE_OFFSETS.length; dir++) {
            if (!mask[dir]) continue;
            int[] so = SOURCE_OFFSETS[dir];
            String k = key(source.getX() + so[0], source.getY() + so[1], source.getZ() + so[2]);
            DustBlock db = map.get(k);
            if (db != null) {
                db.addPowerTarget(source);
            }
        }

        return new ArrayList<>(map.values());
    }

    /**
     * Connectivity checks for dust-dust: horizontal, staircase, blocking, transparency
     */
    private static boolean isAffectedBy(DustBlock A, DustBlock B,
                                        List<BlockingBlockSpec> blocking,
                                        List<TransparentBlockSpec> transparent) {
        int dx = B.getX() - A.getX();
        int dy = B.getY() - A.getY();
        int dz = B.getZ() - A.getZ();
        // Horizontal
        if (dy == 0 && Math.abs(dx) + Math.abs(dz) == 1) return true;
        // Staircase
        if (Math.abs(dy) == 1 && Math.abs(dx) + Math.abs(dz) == 1) {
            // Check blocking-cap
            DustBlock lower = A.getY() < B.getY() ? A : B;
            String capKey = key(lower.getX(), lower.getY() + 1, lower.getZ());
            if (blocking.stream().anyMatch(b -> key(b.x(), b.y(), b.z()).equals(capKey))) {
                return false;
            }
            // Check transparent staircase
            DustBlock higher = (lower == A ? B : A);
            String transKey = key(higher.getX(), higher.getY() - 1, higher.getZ());
            if (transparent.stream().anyMatch(t -> key(t.x(), t.y(), t.z()).equals(transKey))) {
                return A != lower;
            }
            // default
            return true;
        }
        return false;
    }

    private static String key(int x, int y, int z) {
        return x + ":" + y + ":" + z;
    }
}