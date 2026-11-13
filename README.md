# 🎓 Student Workspace (Java CLI)

A comprehensive command-line workspace that consolidates **student notes, assignment deadlines, subject-wise attendance, syllabus progress, and GPA analytics** into a single system.

Built using **Java Collections** and **File I/O** only, with **zero external database dependencies**. All data is stored persistently in local text files.

**Author:** [Your Name] | **Roll No:** [Your Roll No] | **College:** [Your College Name]

---

## 📌 Table of Contents
- [Features](#-features)
- [Tech Stack](#-tech-stack)
- [Project Structure](#-project-structure)
- [Prerequisites](#-prerequisites)
- [How to Run](#-how-to-run)
- [Usage Guide](#-usage-guide)
- [How Persistence Works](#-how-persistence-works)
- [GPA Formula](#-gpa-formula)
- [Attendance Logic](#-attendance-logic)
- [Concepts Demonstrated](#-concepts-demonstrated)
- [Future Improvements](#-future-improvements)

---

## ✨ Features

| Module | What it does |
|---|---|
| 📝 **Notes** | Add, list, keyword-search and delete notes by subject |
| ⏰ **Deadlines** | Add assignment deadlines, auto-sorted by date, with days-left / overdue status, mark as done |
| 📊 **Attendance** | Mark present/absent per subject, percentage report, and how many classes you can skip or must attend to stay at 75% |
| 📚 **Syllabus** | Track topics per subject with progress bars |
| 🎯 **GPA Analytics** | SGPA per semester, CGPA, best/weakest course, and a target-CGPA planner (10-point scale) |
| 🖥️ **Dashboard** | One-screen summary of pending deadlines, low attendance, syllabus progress and CGPA |

---

## 🛠 Tech Stack

- **Language:** Java 17+
- **Collections:** `ArrayList`, `LinkedHashMap`, `TreeMap`
- **File I/O:** `java.nio.file` (`Files.readAllLines`, `Files.write`)
- **Other:** Streams, `java.time` (`LocalDate`), Java records for data models
- **Database:** None (plain text file storage)

---

## 📁 Project Structure

```
Student-workspace/
├── Main.java         # CLI menus and analytics
├── Workspace.java    # Collections + file load/save (persistence)
├── Note.java         # Note data model
├── Deadline.java     # Deadline data model
├── Topic.java        # Syllabus topic data model
├── Course.java       # Course/grade data model
├── README.md
├── .gitignore
└── data/             # Auto-created at runtime (git-ignored)
    ├── notes.txt
    ├── deadlines.txt
    ├── attendance.txt
    ├── syllabus.txt
    └── courses.txt
```

---

## ✅ Prerequisites

- **Java JDK 17 or newer**
- Check your version:
  ```bash
  java -version
  javac -version
  ```
- Download from [adoptium.net](https://adoptium.net) if not installed.

---

## ▶️ How to Run

**1. Clone the repository**
```bash
git clone https://github.com/samad1223/Student-workspace.git
cd Student-workspace
```

**2. Compile**
```bash
mkdir out
javac -d out *.java
```

**3. Run**
```bash
java -cp out Main
```

> 💡 On Windows PowerShell, if `mkdir out` shows an error because the folder exists, just skip that line.

---

## 📖 Usage Guide

When the program starts you will see:

```
=== STUDENT WORKSPACE ===
1. Notes
2. Deadlines
3. Attendance
4. Syllabus Progress
5. GPA Analytics
6. Dashboard
0. Save & Exit
```

| Option | Module | Actions |
|---|---|---|
| 1 | Notes | Add / List / Search / Delete |
| 2 | Deadlines | Add / List / Mark done / Delete |
| 3 | Attendance | Mark class / Report / Remove subject |
| 4 | Syllabus | Add topic / Toggle topic / Progress |
| 5 | GPA Analytics | Add course / List / SGPA-CGPA report / Target planner / Delete |
| 6 | Dashboard | Overall summary |
| 0 | Exit | Saves and quits |

**Tip:** Dates are entered as `YYYY-MM-DD` (example: `2026-12-15`). Grade points are on a 10-point scale (O = 10, A+ = 9, A = 8, B+ = 7, B = 6, C = 5, F = 0).

---

## 💾 How Persistence Works

- Each module has its own file inside the `data/` folder.
- Records are stored as `|`-separated lines, for example `Math|Algebra Notes|Chapter 1 summary`.
- On startup, `Workspace.load()` reads every file into Java Collections.
- After every change,
