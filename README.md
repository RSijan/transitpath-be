# transitpath-be - Belgian Transit Route Optimizer

transitpath-be is a Java command-line application that computes fast public-transit itineraries across multiple Belgian operators using GTFS data.

## Highlights

- Multi-agency GTFS ingestion (DELIJN, SNCB, STIB, TEC)
- Graph-based network model with:
  - Transit edges (trip-based)
  - Walking edges between nearby stops
- A*-based path search with optional user preferences:
  - Less transfers
  - Less walking
  - Avoid specific vehicle types

## Tech Stack

- Java 21+ (works with modern LTS JVMs)
- [univocity-parsers](https://github.com/uniVocity/univocity-parsers) for CSV parsing

## Repository Layout

```text
Main.java
src/
  graphbuilder/
  model/
  parser/
  pathfinder/
  utils/
lib/
```

## GTFS Data Setup

Create the following folder structure at the project root:

```text
GTFS/
  DELIJN/
    routes.csv
    stops.csv
    trips.csv
    stop_times.csv
  SNCB/
    routes.csv
    stops.csv
    trips.csv
    stop_times.csv
  STIB/
    routes.csv
    stops.csv
    trips.csv
    stop_times.csv
  TEC/
    routes.csv
    stops.csv
    trips.csv
    stop_times.csv
```

The `GTFS` directory is intentionally excluded from version control.

## Build

From the project root:

### Linux / macOS

```bash
rm -rf bin && mkdir -p bin
javac -d bin -cp lib/univocity-parsers-2.9.1.jar Main.java src/model/*.java src/parser/*.java src/graphbuilder/*.java src/pathfinder/*.java src/utils/*.java
```

### Windows (PowerShell)

```powershell
if (Test-Path bin) { Remove-Item -Recurse -Force bin }
New-Item -ItemType Directory -Path bin | Out-Null
javac -d bin -cp "lib\univocity-parsers-2.9.1.jar" Main.java src\model\*.java src\parser\*.java src\graphbuilder\*.java src\pathfinder\*.java src\utils\*.java
```

## Run

### Linux / macOS

```bash
java -Xmx4g -cp "bin:lib/univocity-parsers-2.9.1.jar" Main
```

### Windows

```powershell
java -Xmx4g -cp "bin;lib\univocity-parsers-2.9.1.jar" Main
```

## Usage

1. Wait for GTFS loading and graph construction.
2. Enter a starting stop name.
3. Pick a stop from the numbered candidates.
4. Enter a destination stop name.
5. Enter departure time in `HH:MM` format.
6. Optionally choose preferences.
7. Review the computed itinerary.

## Demo

Example run with real GTFS data:

```text
Welcome to transitpath-be!
Please wait while we load the transit data...
Finished parsing transit data successfully. Total time taken in 18.120 s.
Finished graph building successfully. Total time taken: 13.690 s.
Transit data loaded successfully in 31.867 s.

Please enter the starting stop name:
Central
... (candidate stops shown)
You selected: Bruxelles-Central

Please enter the destination stop name:
Midi
... (candidate stops shown)
You selected: Bruxelles-Midi

Please enter the departure time in (HH:MM) format. Example is 09:30 :
09:30

Please select your preference(max. 2) (Example: 53 or 1 or 23):
-> Leave empty for no preferences.

Determining fastest path from Bruxelles-Central to Bruxelles-Midi at 09:30:00
Take SNCB TRAIN IC from Bruxelles-Central (09:32:00) to Bruxelles-Midi (09:39:00)
```

This example shows the complete flow from data loading to itinerary generation.

## Quality Notes

Recent improvements for publication-readiness include:

- Robust terminal input handling (works in IDE and shell terminals)
- Safer graph traversal and edge construction
- Corrected coordinate and time formatting issues
- Modernized repository hygiene with a cleaner `.gitignore`

## License

This project is licensed under the MIT License. See `LICENSE`.
