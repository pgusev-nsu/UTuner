# UTuner
UTuner предназначен для использования разработчиками Java-приложений с целью поиска хороших настроек JVM, существенно улучшающих производительность разрабатываемых и используемых приложений и исследователей производительности JVM. UTuner существенно упростит процесс поиска оптимальных параметров JVM для каждого конкретного приложения, позволит ускорить сами приложения, а также поможет в исследовании влияния параметров JVM на само приложение.

Использование:
```bash
utuner --OPT params_file TESTPerfomanceGC.jar
```
Структура файла параметров:
```text
Mode=Prof
Param1=Value1
Param2=Value2
Param3=Value3
```
Параметры, использующиеся для оптимизации:
```text
Xmx
SurvivorRatio
MaxTenuringThreshold
TargetSurvivorRatio
```

Параметры:
Оптимизация:
```bash
--OPT params_file test_program.jar
```
Профилирование
```bash
--PROF params_file test_program.jar
```

Полученные результаты на тесте TESTPerfomanceGC.jar:
```bash
utuner --OPT params_file.txt TESTPerfomanceGC.jar
```

Performance increase: 27.77 %
