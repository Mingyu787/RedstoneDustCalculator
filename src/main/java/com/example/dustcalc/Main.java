package com.example.dustcalc;

import com.example.dustcalc.input.Instance;
import com.example.dustcalc.input.InstanceLoader;
import com.example.dustcalc.input.JsonInstanceLoader;

/**
 * Entry point for dustCalc: loads an Instance from JSON and prints it.
 */
public class Main {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Usage: java -jar dustcalc.jar <input.json>");
            System.exit(1);
        }
        String inputPath = args[0];
        try {
            InstanceLoader loader = new JsonInstanceLoader(inputPath);
            Instance instance = loader.load();

            System.out.println("=== Loaded Instance ===");
            System.out.println("Power source: " + instance.getPowerSource());
            System.out.println("Wires: " + instance.getWires());
            System.out.println("Transparent blocks: " + instance.getTransparentBlocks());
            System.out.println("Blocking blocks: " + instance.getBlockingBlocks());
        } catch (Exception e) {
            System.err.println("Error loading instance: " + e.getMessage());
            e.printStackTrace();
            System.exit(2);
        }
    }
}