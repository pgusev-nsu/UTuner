package org.nsu.utuner;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class JvmStandartProfiler implements Profiler {

    private static int SITES_MODE = 1;
    private static int CPU_MODE = 2;
    private static String FILENAME = "java.hprof.txt";

    @Override
    public Map<String, String> profile(Map<String, String> configMap)
            throws IOException, ParseFileException, InterruptedException {
        HashMap<String, String> result = new HashMap<>();

        // profile heap
        Process process = Runtime.getRuntime().exec("java -agentlib:hprof=heap=sites -jar "
                + configMap.get("ProgramName") + " " + configMap.get("Parameters"));
        process.waitFor(); // waiting for profiling
        parseResults(SITES_MODE, result);

        //profile cpu time
        process = Runtime.getRuntime().exec("java -agentlib:hprof=cpu=samples -jar "
                + configMap.get("ProgramName") + " " + configMap.get("Parameters"));
        process.waitFor(); // waiting for profiling
        parseResults(CPU_MODE, result);
        return result;
    }

    private void parseResults(int mode, Map<String, String> result)
            throws ParseFileException{

        try{
            BufferedReader reader = new BufferedReader(new FileReader(FILENAME));
            String readed;
            StringBuilder information = new StringBuilder();

            if(mode == SITES_MODE) {

                // find the table with useful information
                do{
                    readed = reader.readLine();
                    if (readed == null)
                        throw new ParseFileException("Cannot find sites table");
                } while (!readed.contains("SITES BEGIN"));

                // skip header
                reader.readLine();
                reader.readLine();

                for (int i = 1; i <= 10; i++) {
                    readed = reader.readLine();
                    if ((readed == null) || (readed.equals("SITES END")))
                        break;
                    String[] tmp = readed.split(" +");

                    information.append(tmp[9]); // class name
                    information.append(": ");
                    information.append(tmp[7]); // number of objects
                    information.append(" objects, ");
                    information.append(tmp[6]); // allocated bytes
                    information.append(" allocated bytes, ");
                    information.append(tmp[2]); // % of memory 2
                    information.append(" of memory");

                    result.put("Top" + i + "GreedyClass", information.toString());
                    information.delete(0, information.length());
                }
            }
            if (mode == CPU_MODE){

                // find the table with useful information
                do{
                    readed = reader.readLine();
                    if (readed == null)
                        throw new ParseFileException("Cannot find sites table");
                } while (!readed.contains("CPU SAMPLES BEGIN"));

                // skip header
                reader.readLine();

                for (int i = 1; i <= 10; i++){
                    readed = reader.readLine();
                    if ((readed == null) || (readed.equals("CPU SAMPLES END")))
                        break;
                    String[] tmp = readed.split(" +");

                    information.append(tmp[6]); // method name
                    information.append(": count: ");
                    information.append(tmp[4]); // count of threads (not sure :D)
                    information.append(", ");
                    information.append(tmp[2]); // cpu time (%)
                    information.append(" of cpu time");

                    result.put("Top" + i + "SlowMethod", information.toString());
                    information.delete(0, information.length());
                }
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
}
