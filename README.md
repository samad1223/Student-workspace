# Student Workspace (Java CLI)

A command-line workspace that brings **notes, assignment deadlines, subject-wise attendance,
syllabus progress and GPA analytics** into one system.

Built with **Java Collections** and **File I/O** only. There are no external libraries and no database.
All data persists in plain text files inside a local `data/` folder.

## Features
| Module | What it does |
|---|---|
| Notes | Add, list, keyword-search and delete notes by subject |
| Deadlines | Add deadlines, auto-sorted by date, with days-left / overdue status, mark done |
| Attendance | Mark present/absent per subject, % report, how many classes you can skip or must attend to stay at 75% |
| Syllabus | Track topics per subject with progress bars |
| GPA Analytics | SGPA per semester, CGPA, best/weakest course, target-CGPA planner (10-point scale) |
| Dashboard | One-screen summary of everything |

## Tech
- Java 17+
- `ArrayList`, `LinkedHashMap`, `TreeMap`, Streams, `java.time`, `java.nio.file`
- Java records for the data models

## Project structure
```
src/
  Main.java        CLI menus and analytics
  Workspace.java   Collections + file load/save
  Note.java  Deadline.java  Topic.java  Course.java   (data models)
data/              auto-created at runtime (git-ignored)
```

## Run
```bash
mkdir out
javac -d out src/*.java
java -cp out Main
```

## How persistence works
Each module is stored in its own file (`notes.txt`, `deadlines.txt`, `attendance.txt`,
`syllabus.txt`, `courses.txt`) as `|`-separated lines. Data loads at startup and saves after every
change and on exit.

## GPA formula
`GPA = Σ(grade point × credits) / Σ credits`

## Possible future work
Export reports to CSV, reminders for deadlines, a GUI or web front-end.
