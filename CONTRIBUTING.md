# Contributing

Thanks for your interest in contributing.

## Development Workflow

1. Create a feature branch.
2. Keep changes scoped and focused.
3. Ensure the project compiles before opening a PR.
4. Include a clear description of what changed and why.

## Local Build

Linux/macOS:

```bash
rm -rf bin && mkdir -p bin
javac -d bin -cp lib/univocity-parsers-2.9.1.jar Main.java src/model/*.java src/parser/*.java src/graphbuilder/*.java src/pathfinder/*.java src/utils/*.java
```

Windows:

```powershell
if (Test-Path bin) { Remove-Item -Recurse -Force bin }
New-Item -ItemType Directory -Path bin | Out-Null
javac -d bin -cp "lib\univocity-parsers-2.9.1.jar" Main.java src\model\*.java src\parser\*.java src\graphbuilder\*.java src\pathfinder\*.java src\utils\*.java
```

## Style Guidelines

- Prefer clear, descriptive names.
- Handle invalid input defensively.
- Avoid introducing unnecessary dependencies.

## Data

Do not commit GTFS datasets. The `GTFS/` folder is ignored by git.
