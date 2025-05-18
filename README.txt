0) Make sure you have java installed on your device and make sure you have the GTFS directory within the project folder
   with the four agency directories.

1) Open project folder.

2) Compile with the compilation command given below according to your OS.

3) Run with the run command given below according to your OS.

4) Wait a bit, so that it finishes loading transit data.

5) Then you can enter the name of the starting stop of your travel, press enter.
   All related stops will be shown, enter the corresponding number to the stop name that you wanna start from, and press enter.
   If you want to search for a different stop, you can enter the last shown number and press enter.

6) Then you'll be demanded for the target stop of your travel. Do the same as the previous step for target stop of your travel.

7) Enter the departure time in HH:MM format, then press enter.

8) You'll be shown the most optimal path.

9) You can either ask for another path by typing y, and then pressing enter, or quit by typing n, and then pressing enter.

## [on Windows]
Inside project folder:
# For compilation:
javac -d bin -cp "lib\univocity-parsers-2.9.1.jar" Main.java .\src\model\*.java .\src\parser\*.java .\src\graphbuilder\*.java .\src\pathfinder\*.java .\src\utils\*.java
# To run:
java -Xmx4g -cp "bin;lib\univocity-parsers-2.9.1.jar" Main


## [on Linux/Mac]
Inside project folder:
# For compilation:
javac -d bin -cp lib/univocity-parsers-2.9.1.jar Main.java src/model/*.java ./src/parser/*.java ./src/graphbuilder/*.java src/pathfinder/*.java src/utils/*.java 

# To run:
java -Xmx4g -cp "bin:lib/univocity-parsers-2.9.1.jar" Main

