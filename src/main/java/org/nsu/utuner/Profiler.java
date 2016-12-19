package org.nsu.utuner;

import java.io.IOException;
import java.util.Map;


public interface Profiler {
    public Map<String, String> Profile(Map<String, String> configMap) throws IOException, ParseFileException,
            InterruptedException;
}
