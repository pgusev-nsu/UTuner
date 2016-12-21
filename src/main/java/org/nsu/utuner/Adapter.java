package org.nsu.utuner;

import javax.json.Json;
import javax.json.stream.JsonParser;
import java.io.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Adapter {
    private static final String command = "jvm_tuner.py";
    private static final String openTunerOutput = "jvm_final_config.json";

    public static Map<String, String> optimize(Map<String, String> parameters, String program, int stopTime) throws AdapterException {
        ProcessBuilder processBuilder = build(program, stopTime);

        processBuilder.redirectErrorStream(true);
        processBuilder.redirectOutput();

        Map<String, String> parametersMap;

        try {
            Process process = processBuilder.start();
            process.waitFor();

            parametersMap = parseResults(new File(openTunerOutput));
        } catch (Exception e) {
            throw new AdapterException();
        }
        return parametersMap;
    }

    private static ProcessBuilder build(String program, int stopTime){
        List<String> commands = new LinkedList<>();
        commands.add("python");
        commands.add(command);
        commands.add(program);

        int index = program.lastIndexOf('.');
        if(index > 0){
            String substr = program.substring(index + 1);
            if("jar".equals(substr)){
                commands.add("--jar=True");
            }
        }
        commands.add("--no-dups");
        commands.add("--stop-after=" + stopTime);

        return new ProcessBuilder(commands);
    }

    private static Map<String, String> parseResults(File file) throws FileNotFoundException {
        JsonParser parser = Json.createParser(new FileReader(file));
        Map<String, String> parametersMap = new HashMap<>();

        if(parser.hasNext()){
            parser.next();
            while(parser.hasNext() && parser.next() == JsonParser.Event.KEY_NAME){
                String key = parser.getString();
                parser.next();
                String value = parser.getString();
                parametersMap.put(key, value);
            }
        }

        parser.close();
        return parametersMap;
    }
}
