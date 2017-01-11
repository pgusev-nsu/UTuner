package org.nsu.utuner;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import static java.lang.ClassLoader.getSystemClassLoader;

public class Parser {
    private static String[] correctModes = {"AI", "OPT", "PROF"};

    public static Map<String, String> parse(String filename) throws IOException, ParseFileException {
        try (InputStream in = new FileInputStream(filename)) {
            return parseStream(in);
        }
    }

    public static Map<String, String> parseStream(InputStream stream) throws IOException, ParseFileException {
        Map<String, String> configMap;
        Properties p = new Properties();
        p.load(stream);
        Set<String> comSet = p.stringPropertyNames();

        configMap = new HashMap<>(comSet.size());
        for (String str : comSet) {
            String property = p.getProperty(str);
            if (property != null) {
                configMap.put(str, property);
            }
        }

        validate(configMap);
        return configMap;
    }

    private static void validate(Map<String, String> configMap) throws ParseFileException {
        String mode = configMap.get("Mode");
        if(mode == null){
            throw new ParseFileException("There is no \"Mode\" property in the configuration file");
        }

        for(String m : correctModes){
            if(mode.toUpperCase().equals(m)){
                return;
            }
        }

        throw new ParseFileException("Incorrect \"Mode\" property in the configuration file");
    }
}
