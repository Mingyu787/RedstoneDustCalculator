package com.example.dustcalc.input;

import java.util.List;

/**
 * The raw circuit description:
 *   • one power source
 *   • a list of dust blocks (initial positions and powers)
 *   • transparent blocks
 *   • blocking blocks
 */
public record Instance(
        PowerSourceSpec powerSource,
        List<DustBlockSpec> dustBlocks,
        List<TransparentBlockSpec> transparentBlocks,
        List<BlockingBlockSpec> blockingBlocks
) {}