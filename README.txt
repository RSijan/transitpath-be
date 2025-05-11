## on Windows
For compilation:
javac -d bin -cp "lib\univocity-parsers-2.9.1.jar" Main.java .\src\model\*.java .\src\pathfinder\*.java .\src\utils\*.java

To run:
java -cp "bin;lib\univocity-parsers-2.9.1.jar" Main

## on linux/mac

For compilation:
javac -d bin -cp lib/univocity-parsers-2.9.1.jar Main.java src/model/*.java src/pathfinder/*.java src/utils/*.java

To run:
java -Xms1g -Xmx4g -cp "bin:lib/univocity-parsers-2.9.1.jar" Main
