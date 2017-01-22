# UTuner
UTuner It intended for use by developers of Java-based applications in order to find a good JVM settings, greatly improving the performance of applications developed and used by researchers of JVM performance. UTuner significantly simplify the process of finding the optimal parameters of JVM for each particular application, to speed up the applications themselves, as well as help in the study of the impact of JVM parameters on the application itself.

Usage:
```bash
java -jar utuner-1.0.jar --OPT file.txt TESTPerfomanceGC.jar
```
Stucture of parameters file:
```text
Mode=Prof
Param1=Value1
Param2=Value2
Param3=Value3
```
List of parameters used for optimization:
```text
Xmx
SurvivorRatio
MaxTenuringThreshold
TargetSurvivorRatio
```
Also, more other optimization parameters can be easily added to the UTuner when needed.

Parameters description:
Optimization:
```bash
--OPT params_file test_program.jar
```
Profiling:
```bash
--PROF params_file test_program.jar
```

Results we got on test "TESTPerfomanceGC.jar":
```bash
java -jar utuner-1.0.jar --OPT params_file.txt TESTPerfomanceGC.jar
```

Performance increase: 27.77 %
