package com.example.dustcalc;

import com.example.dustcalc.input.*;
import com.example.dustcalc.model.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Entry point for dustCalc: loads an Instance from JSON,
 * builds the connectivity graph, applies update ordering, and prints both.
 */
public class Main {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Usage: java -jar dustcalc.jar <input.json>");
            System.exit(1);
        }
        String inputPath = args[0];
        try {
            // Load the circuit instance
            InstanceLoader loader = new JsonInstanceLoader(inputPath);
            Instance instance = loader.load();

            System.out.println("=== Loaded Instance ===");
            System.out.println("Power source: " + instance.powerSource());
            System.out.println("Dust blocks: " + instance.dustBlocks());
            System.out.println("Transparent blocks: " + instance.transparentBlocks());
            System.out.println("Blocking blocks: " + instance.blockingBlocks());

            // Build raw connectivity
            CircuitGraphBuilder builder = new CircuitGraphBuilder();
            List<DustBlock> graph = builder.build(instance);

            System.out.println("=== Connectivity Graph ===");
            graph.forEach(b -> {
                String coords = b.getX() + "," + b.getY() + "," + b.getZ();
                String targets = b.getPowerTargets().stream()
                        .map(t -> t.getX() + "," + t.getY() + "," + t.getZ())
                        .collect(Collectors.joining(" | "));
                System.out.printf("%-12s -> %s%n", coords, targets);
            });

            // Apply update ordering
            UpdateOrderCalculator orderCalc = new UpdateOrderCalculator();
            orderCalc.applyOrdering(graph);

            System.out.println("=== Update Order Graph ===");
            graph.forEach(b -> {
                String coords = b.getX() + "," + b.getY() + "," + b.getZ();
                String updates = b.getUpdateTargets().stream()
                        .map(t -> t.getX() + "," + t.getY() + "," + t.getZ())
                        .collect(Collectors.joining(" | "));
                System.out.printf("%-12s -> %s%n", coords, updates);
            });

            // Assign sequential IDs to each DustBlock
            Map<String,Integer> idMap = new LinkedHashMap<>();
            for (int i = 0; i < graph.size(); i++) {
                DustBlock b = graph.get(i);
                String key = b.getX() + ":" + b.getY() + ":" + b.getZ();
                idMap.put(key, i + 1);
            }

            // Print Trace IDs mapping
            System.out.println("=== Trace IDs ===");
            idMap.forEach((key, id) -> {
                String[] parts = key.split(":");
                System.out.printf("[ID=%d] @ (%s, %s, %s)%n", id, parts[0], parts[1], parts[2]);
            });

            // Run the PhaseTracer to record every update-call
            UpdateTracer tracer = new UpdateTracer(graph);
            List<UpdateTracer.TraceEntry> traceLog = tracer.traceAll(instance.powerSource());

            // Print Trace Log
            int[] powerArr = tracer.getInitialPowers();
            System.out.println("=== Trace Log ===");
            for (int i = 0; i < traceLog.size(); i++) {
                UpdateTracer.TraceEntry e = traceLog.get(i);
                String from = e.from();
                String to = e.to();
                String fromLabel = from.equals("SOURCE")
                        ? "[SOURCE]"
                        : String.format("[%d] (%s)", idMap.get(from), from.replace(':', ','));
                String toLabel = String.format("[%d] (%s)", idMap.get(to), to.replace(':', ','));
                // Update the powerArr for the 'to' block
                int toId = idMap.get(to);
                powerArr[toId - 1] = e.power();

                // Print the entry with current global powers
                System.out.printf("%d:\t%-20s\t→\t%-16s\t@ power=%02d    %s%n", i,
                        fromLabel, toLabel, e.power(), Arrays.toString(powerArr));
            }

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            System.exit(2);
        }
    }
}