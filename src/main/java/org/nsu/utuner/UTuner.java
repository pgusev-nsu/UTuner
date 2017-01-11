package org.nsu.utuner;

import java.io.*;
import java.util.*;

public class UTuner {
    private static String PROF_FILE = "profiler_results.txt";
    private static String OPT_FILE = "optimizer_results.txt";
    private static String TIME_FILE = "time.txt";

    public static void main(String[] args){
        String mode = args[0].toUpperCase();
        switch(mode){
            case "--OPT":
                optimize(args);
                break;
            case "--PROF":
                profile(args);
                break;
            case "--AI":
                break;
            default:
                System.err.println("Incorrect mode");
        }
    }

    private static void optimize(String[] args){
        Map<String, String> parameters;
        try {
            parameters = Parser.parse(args[1]);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return;
        }

        String[] programParams = Arrays.copyOfRange(args, 3, args.length);

        Map<String, String> optimalParameters;
        try {
            optimalParameters = Adapter.optimize(parameters, args[2], programParams, 100);
        } catch (AdapterException e) {
            System.err.println("Can not optimize the program");
            return;
        }

        double performance;
        try {
            performance = countPerformance(args[2], programParams);
        } catch (Exception e) {
            return;
        }

        try {
            writeResultsToFile(optimalParameters, OPT_FILE);
            System.out.println("Optimal parameters are written to " + OPT_FILE);
        } catch (IOException e) {
            System.err.println("Can not write parameters to file");
        }

        double increase = (performance * 100 - 100);
        System.out.println("Performance increase: " + String.format("%(.2f", increase) + " %");
    }

    private static void profile(String[] args){
        Profiler profiler = new JvmStandartProfiler();

        Map<String, String> map = new HashMap<>();
        map.put("ProgramName", args[1]);

        String params = createCommand(Arrays.copyOfRange(args, 2, args.length));
        map.put("Parameters", params);

        Map<String, String> result;
        try {
            result = profiler.profile(map);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return;
        }

        try {
            writeResultsToFile(result, PROF_FILE);
            System.out.println("Profiler results are written to " + PROF_FILE);
        } catch (IOException e) {
            System.err.println("Can not write results to file");
        }
    }

    private static double countPerformance(String prog, String[] params) throws IOException, InterruptedException {
        List<String> paramsList = new LinkedList<>();
        paramsList.add("java");
        paramsList.add("-jar");
        paramsList.add(prog);
        for(String p : params){
            paramsList.add(p);
        }

        ProcessBuilder processBuilder = new ProcessBuilder(paramsList);

        long startTime = System.currentTimeMillis();
        Process process = processBuilder.start();
        process.waitFor();
        long endTime = System.currentTimeMillis();

        double initialTime = ((double)(endTime - startTime)) / 1000;

        BufferedReader reader = new BufferedReader(new FileReader(new File(TIME_FILE)));
        double optimizedTime = Double.parseDouble(reader.readLine());
        reader.close();

        return initialTime / optimizedTime;
    }

    private static String createCommand(String[] params){
        StringBuilder builder = new StringBuilder();
        for(int i = 2; i < params.length; ++i){
            builder.append(" ");
            builder.append(params[i]);
        }
        return builder.toString();
    }

    private static void writeResultsToFile(Map<String, String> result, String filename) throws IOException {
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(new File(filename)))) {
            Set<String> keySet = result.keySet();
            for (String s : keySet) {
                writer.write(s + "=" + result.get(s));
                writer.newLine();
            }
        }
    }
}
