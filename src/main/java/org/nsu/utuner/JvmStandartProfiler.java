package org.nsu.utuner;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class JvmStandartProfiler implements Profiler{

    public Map<String, String> Profile(Map<String, String> configMap) throws IOException, ParseFileException {
        String fileName = "java.hprof.txt";
        Runtime.getRuntime().exec("java -agentlib:hprof=heap=dump -jar " + configMap.get("ProgramName ")
                + configMap.get("Parameters"));
        return parseResults(fileName);

    }

    private Map<String, String> parseResults(String fileName) throws ParseFileException{
        HashMap<String, String> result = new HashMap<>();

        try{
            BufferedReader reader = new BufferedReader(new FileReader(fileName));
            String readed;

            // find the table with useful information
            do{
                readed = reader.readLine();
                if (readed == null)
                    throw new ParseFileException("Cannot find sites table");
            } while (!readed.contains("SITES BEGIN"));

            // skip header
            reader.readLine();
            reader.readLine();

            // parse top 10 of object ordered by memory using
            // TODO: parse table to the Map, parse CPU time from another profiling

        }
        catch (IOException e){
            e.printStackTrace();
        }

        return result;
    }
}
