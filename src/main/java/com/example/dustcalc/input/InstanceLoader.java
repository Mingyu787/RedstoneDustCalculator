package com.example.dustcalc.input;

import java.io.IOException;

/**
 * Common interface for loading an Instance from input sources.
 */
public interface InstanceLoader {
    /**
     * Loads and parses a circuit Instance.
     * @return the parsed Instance
     * @throws IOException on read/parse failure
     */
    Instance load() throws IOException;
}