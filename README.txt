## on Windows
For compilation:
javac -d bin -cp .\lib\opencsv-5.10.jar Main.java .\src\model\*.java .\src\pathfinder\*.java .\src\utils\*.java

To run:
java -cp "bin;lib\opencsv-5.10.jar;lib\commons-lang3-3.12.0.jar" Main

## on linux/mac

For compilation:
javac -d bin -cp lib/opencsv-5.10.jar Main.java src/model/*.java src/pathfinder/*.java src/utils/*.java

To run:
java -Xms1g -Xmx4g -cp "bin:lib/opencsv-5.10.jar:lib/commons-lang3-3.12.0.jar" Main
