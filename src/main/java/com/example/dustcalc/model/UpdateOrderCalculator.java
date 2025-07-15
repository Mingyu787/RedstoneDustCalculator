package com.example.dustcalc.model;

import java.util.*;

/**
 * Computes and applies the redstone-dust update ordering for each DustBlock.
 * Each block independently generates an ordered list of neighbor updates:
 *  - Compute 7 "second-order" positions (self + 6 directions) in perturbed order
 *  - For each second-order position, compute 6 "first-order" neighbor updates (-X,+X,-Y,+Y,-Z,+Z)
 *  - Deduplicate while preserving order and map to existing DustBlocks
 */
public class UpdateOrderCalculator {

    // Relative offsets for the seven second-order positions
    private static final int[][] SECOND_ORDER_OFFSETS = {
            { 0,  0,  0}, {-1,  0,  0}, { 1,  0,  0},
            { 0, -1,  0}, { 0,  1,  0}, { 0,  0, -1}, { 0,  0,  1}
    };

    // Tie-break order for offsets with equal index
    private static final int[] TIE_BREAK_ORDER = {0, 3, 4, 5, 6, 1, 2};

    // Exactly six first-order neighbor offsets, in fixed order
    private static final int[][] FIRST_ORDER_OFFSETS = PowerSourceBlock.getSourceOffsets();

    /** Applies ordering to each DustBlock's updateTargets. */
    public void applyOrdering(List<DustBlock> graph) {
        // Build coordinate lookup
        Map<String, DustBlock> lookup = new HashMap<>();
        for (DustBlock b : graph) {
            lookup.put(key(b.getX(), b.getY(), b.getZ()), b);
        }
        // For each block, generate all 42 update orders
        for (DustBlock b : graph) {
            b.clearUpdateTargets();
            List<int[]> secondOrder = computeSecondOrderOffsets(b.getX(), b.getY(), b.getZ());
            for (int[] base : secondOrder) {
                int bx = b.getX() + base[0];
                int by = b.getY() + base[1];
                int bz = b.getZ() + base[2];
                List<int[]> firsts = computeFirstOrderOffsets(bx, by, bz);
                for (int[] f : firsts) {
                    String k = key(f[0], f[1], f[2]);
                    if (lookup.containsKey(k)) {
                        b.addUpdateTarget(lookup.get(k));
                    }
                }
            }
        }
    }

    /**
     * Computes perturbed second-order offsets in correct order.
     */
    private List<int[]> computeSecondOrderOffsets(int x, int y, int z) {
        // Pair offsets with their hash index and ordinal
        record Off(int[] off, int idx, int ord) {}
        List<Off> list = new ArrayList<>();
        for (int i = 0; i < SECOND_ORDER_OFFSETS.length; i++) {
            int[] off = SECOND_ORDER_OFFSETS[i];
            int idx = computeIndex(x + off[0], y + off[1], z + off[2]);
            list.add(new Off(off, idx, i));
        }
        // Sort by idx, then tie-break via fixed order
        list.sort((a, b) -> {
            int c = Integer.compare(a.idx, b.idx);
            if (c != 0) return c;
            for (int tie : TIE_BREAK_ORDER) {
                if (tie == a.ord) return -1;
                if (tie == b.ord) return  1;
            }
            return 0;
        });
        // Extract offsets
        List<int[]> result = new ArrayList<>();
        for (Off o : list) result.add(o.off);
        return result;
    }

    /** Generates six first-order neighbor offsets from (x,y,z). */
    private List<int[]> computeFirstOrderOffsets(int x, int y, int z) {
        List<int[]> result = new ArrayList<>();
        for (int[] off : FIRST_ORDER_OFFSETS) {
            result.add(new int[]{x + off[0], y + off[1], z + off[2]});
        }
        return result;
    }

    /** Computes dust perturbation index: K=x+31y+961z, h=K^(K>>>16), index=h&0xF. */
    private int computeIndex(int x, int y, int z) {
        int K = x + 31 * y + 961 * z;
        int h = K ^ (K >>> 16);
        return h & 0xF;
    }

    private String key(int x, int y, int z) {
        return x + ":" + y + ":" + z;
    }
}