package com.example.dustcalc.input;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;

/**
 * Loads an Instance from a JSON file via Jackson.
 */
public class JsonInstanceLoader implements InstanceLoader {
    private final String inputPath;
    private final ObjectMapper mapper = new ObjectMapper().registerModule(new ParameterNamesModule());

    public JsonInstanceLoader(String inputPath) {
        this.inputPath = inputPath;
    }

    @Override
    public Instance load() throws IOException {
        return mapper.readValue(new File(inputPath), Instance.class);
    }
}