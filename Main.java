import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class Main {
    static final Scanner in = new Scanner(System.in);
    static final Workspace ws = new Workspace();
    static final double ATTENDANCE_MIN = 75.0;

    public static void main(String[] args) {
        ws.load();
        System.out.println("Welcome to Student Workspace (data saved in ./data)");
        while (true) {
            System.out.println("\n=== STUDENT WORKSPACE ===");
            System.out.println("1. Notes\n2. Deadlines\n3. Attendance\n4. Syllabus Progress");
            System.out.println("5. GPA Analytics\n6. Dashboard\n0. Save & Exit");
            switch (ask("Choose")) {
                case "1" -> notesMenu();
                case "2" -> deadlinesMenu();
                case "3" -> attendanceMenu();
                case "4" -> syllabusMenu();
                case "5" -> gpaMenu();
                case "6" -> dashboard();
                case "0" -> { ws.save(); System.out.println("Saved. Goodbye!"); return; }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    // ---------- input helpers ----------
    static String ask(String prompt) {
        System.out.print(prompt + ": ");
        if (!in.hasNextLine()) { ws.save(); System.exit(0); }
        return in.nextLine().trim().replace("|", "/"); // '|' is our file delimiter
    }

    static int askInt(String prompt) {
        while (true) {
            try { return Integer.parseInt(ask(prompt)); }
            catch (NumberFormatException e) { System.out.println("Please enter a whole number."); }
        }
    }

    static double askDouble(String prompt) {
        while (true) {
            try { return Double.parseDouble(ask(prompt)); }
            catch (NumberFormatException e) { System.out.println("Please enter a number."); }
        }
    }

    static LocalDate askDate(String prompt) {
        while (true) {
            try { return LocalDate.parse(ask(prompt + " (YYYY-MM-DD)")); }
            catch (Exception e) { System.out.println("Invalid date format."); }
        }
    }

    static boolean validIndex(int i, int size) {
        if (i >= 1 && i <= size) return true;
        System.out.println("Invalid number.");
        return false;
    }

    // ---------- 1. NOTES ----------
    static void notesMenu() {
        while (true) {
            System.out.println("\n-- NOTES -- 1.Add 2.List 3.Search 4.Delete 0.Back");
            switch (ask("Choose")) {
                case "1" -> {
                    ws.notes.add(new Note(ask("Subject"), ask("Title"), ask("Note")));
                    System.out.println("Note added.");
                }
                case "2" -> printNotes(ws.notes);
                case "3" -> {
                    String k = ask("Keyword").toLowerCase();
                    printNotes(ws.notes.stream()
                            .filter(n -> (n.subject() + n.title() + n.body()).toLowerCase().contains(k))
                            .toList());
                }
                case "4" -> {
                    printNotes(ws.notes);
                    int i = askInt("Note number to delete");
                    if (validIndex(i, ws.notes.size())) ws.notes.remove(i - 1);
                }
                case "0" -> { ws.save(); return; }
                default -> System.out.println("Invalid choice.");
            }
            ws.save();
        }
    }

    static void printNotes(List<Note> list) {
        if (list.isEmpty()) { System.out.println("No notes found."); return; }
        for (int i = 0; i < list.size(); i++) {
            Note n = list.get(i);
            System.out.printf("%d. [%s] %s%n    %s%n", i + 1, n.subject(), n.title(), n.body());
        }
    }

    // ---------- 2. DEADLINES ----------
    static void deadlinesMenu() {
        while (true) {
            System.out.println("\n-- DEADLINES -- 1.Add 2.List 3.Mark done 4.Delete 0.Back");
            switch (ask("Choose")) {
                case "1" -> {
                    ws.deadlines.add(new Deadline(ask("Subject"), ask("Title"), askDate("Due date"), false));
                    System.out.println("Deadline added.");
                }
                case "2" -> printDeadlines();
                case "3" -> {
                    printDeadlines();
                    int i = askInt("Number to mark done");
                    if (validIndex(i, ws.deadlines.size())) {
                        Deadline d = ws.deadlines.get(i - 1);
                        ws.deadlines.set(i - 1, new Deadline(d.subject(), d.title(), d.due(), true));
                    }
                }
                case "4" -> {
                    printDeadlines();
                    int i = askInt("Number to delete");
                    if (validIndex(i, ws.deadlines.size())) ws.deadlines.remove(i - 1);
                }
                case "0" -> { ws.save(); return; }
                default -> System.out.println("Invalid choice.");
            }
            ws.save();
        }
    }

    static void printDeadlines() {
        if (ws.deadlines.isEmpty()) { System.out.println("No deadlines."); return; }
        ws.deadlines.sort(Comparator.comparing(Deadline::due));
        LocalDate today = LocalDate.now();
        for (int i = 0; i < ws.deadlines.size(); i++) {
            Deadline d = ws.deadlines.get(i);
            long days = ChronoUnit.DAYS.between(today, d.due());
            String status = d.done() ? "DONE" : days < 0 ? "OVERDUE by " + (-days) + "d"
                    : days == 0 ? "DUE TODAY" : days + " day(s) left";
            System.out.printf("%d. [%s] %s - due %s (%s)%n", i + 1, d.subject(), d.title(), d.due(), status);
        }
    }

    // ---------- 3. ATTENDANCE ----------
    static void attendanceMenu() {
        while (true) {
            System.out.println("\n-- ATTENDANCE -- 1.Mark class 2.Report 3.Remove subject 0.Back");
            switch (ask("Choose")) {
                case "1" -> {
                    String s = ask("Subject");
                    if (s.isEmpty()) break;
                    boolean present = ask("Present? (y/n)").equalsIgnoreCase("y");
                    int[] rec = ws.attendance.computeIfAbsent(s, k -> new int[2]);
                    if (present) rec[0]++;
                    rec[1]++;
                    System.out.printf("%s: %d/%d (%.1f%%)%n", s, rec[0], rec[1], pct(rec));
                }
                case "2" -> attendanceReport();
                case "3" -> ws.attendance.remove(ask("Subject to remove"));
                case "0" -> { ws.save(); return; }
                default -> System.out.println("Invalid choice.");
            }
            ws.save();
        }
    }

    static double pct(int[] r) { return r[1] == 0 ? 0 : 100.0 * r[0] / r[1]; }

    static void attendanceReport() {
        if (ws.attendance.isEmpty()) { System.out.println("No attendance data."); return; }
        System.out.printf("%-18s %8s %8s  %s%n", "Subject", "Attended", "Total", "Status");
        ws.attendance.forEach((s, r) -> {
            double p = pct(r);
            String status;
            if (p >= ATTENDANCE_MIN) {
                int canSkip = (int) Math.floor(r[0] / (ATTENDANCE_MIN / 100) - r[1]);
                status = String.format("%.1f%% OK (can skip %d)", p, Math.max(canSkip, 0));
            } else {
                int need = (int) Math.ceil((ATTENDANCE_MIN / 100 * r[1] - r[0]) / (1 - ATTENDANCE_MIN / 100));
                status = String.format("%.1f%% LOW (attend next %d)", p, need);
            }
            System.out.printf("%-18s %8d %8d  %s%n", s, r[0], r[1], status);
        });
    }

    // ---------- 4. SYLLABUS ----------
    static void syllabusMenu() {
        while (true) {
            System.out.println("\n-- SYLLABUS -- 1.Add topic 2.Toggle topic 3.Progress 0.Back");
            switch (ask("Choose")) {
                case "1" -> ws.topics.add(new Topic(ask("Subject"), ask("Topic"), false));
                case "2" -> {
                    printTopics();
                    int i = askInt("Topic number to toggle");
                    if (validIndex(i, ws.topics.size())) {
                        Topic t = ws.topics.get(i - 1);
                        ws.topics.set(i - 1, new Topic(t.subject(), t.name(), !t.done()));
                    }
                }
                case "3" -> syllabusProgress();
                case "0" -> { ws.save(); return; }
                default -> System.out.println("Invalid choice.");
            }
            ws.save();
        }
    }

    static void printTopics() {
        if (ws.topics.isEmpty()) { System.out.println("No topics."); return; }
        for (int i = 0; i < ws.topics.size(); i++) {
            Topic t = ws.topics.get(i);
            System.out.printf("%d. [%s] [%s] %s%n", i + 1, t.done() ? "x" : " ", t.subject(), t.name());
        }
    }

    static void syllabusProgress() {
        Map<String, int[]> m = new TreeMap<>(); // subject -> {done, total}
        for (Topic t : ws.topics) {
            int[] r = m.computeIfAbsent(t.subject(), k -> new int[2]);
            if (t.done()) r[0]++;
            r[1]++;
        }
        if (m.isEmpty()) { System.out.println("No syllabus data."); return; }
        m.forEach((s, r) -> System.out.printf("%-18s %s %d/%d (%.0f%%)%n", s, bar(pct(r)), r[0], r[1], pct(r)));
    }

    static String bar(double percent) {
        int filled = (int) Math.round(percent / 5);
        return "[" + "#".repeat(filled) + "-".repeat(20 - filled) + "]";
    }

    // ---------- 5. GPA ----------
    static void gpaMenu() {
        while (true) {
            System.out.println("\n-- GPA ANALYTICS (10-point scale) --");
            System.out.println("1.Add course 2.List 3.SGPA/CGPA report 4.Target CGPA planner 5.Delete 0.Back");
            switch (ask("Choose")) {
                case "1" -> {
                    int sem = askInt("Semester");
                    String name = ask("Course name");
                    int cr = askInt("Credits");
                    double gp = askDouble("Grade point (0-10)");
                    if (gp < 0 || gp > 10 || cr <= 0) System.out.println("Invalid credits or grade point.");
                    else ws.courses.add(new Course(sem, name, cr, gp));
                }
                case "2" -> printCourses();
                case "3" -> gpaReport();
                case "4" -> targetPlanner();
                case "5" -> {
                    printCourses();
                    int i = askInt("Number to delete");
                    if (validIndex(i, ws.courses.size())) ws.courses.remove(i - 1);
                }
                case "0" -> { ws.save(); return; }
                default -> System.out.println("Invalid choice.");
            }
            ws.save();
        }
    }

    static void printCourses() {
        if (ws.courses.isEmpty()) { System.out.println("No courses."); return; }
        for (int i = 0; i < ws.courses.size(); i++) {
            Course c = ws.courses.get(i);
            System.out.printf("%d. Sem %d | %-20s | %d cr | GP %.1f%n",
                    i + 1, c.semester(), c.name(), c.credits(), c.gradePoint());
        }
    }

    static double gpa(Collection<Course> cs) {
        double pts = 0; int cr = 0;
        for (Course c : cs) { pts += c.gradePoint() * c.credits(); cr += c.credits(); }
        return cr == 0 ? 0 : pts / cr;
    }

    static int totalCredits() { return ws.courses.stream().mapToInt(Course::credits).sum(); }

    static void gpaReport() {
        if (ws.courses.isEmpty()) { System.out.println("No courses."); return; }
        Map<Integer, List<Course>> bySem = new TreeMap<>();
        for (Course c : ws.courses) bySem.computeIfAbsent(c.semester(), k -> new ArrayList<>()).add(c);
        bySem.forEach((s, l) -> System.out.printf("Semester %d SGPA: %.2f%n", s, gpa(l)));
        System.out.printf("CGPA: %.2f  (credits: %d)%n", gpa(ws.courses), totalCredits());
        Course best = Collections.max(ws.courses, Comparator.comparingDouble(Course::gradePoint));
        Course worst = Collections.min(ws.courses, Comparator.comparingDouble(Course::gradePoint));
        System.out.println("Best course : " + best.name() + " (" + best.gradePoint() + ")");
        System.out.println("Weakest     : " + worst.name() + " (" + worst.gradePoint() + ")");
    }

    static void targetPlanner() {
        double target = askDouble("Target CGPA");
        int next = askInt("Credits in upcoming semester(s)");
        if (next <= 0) { System.out.println("Credits must be positive."); return; }
        double pts = gpa(ws.courses) * totalCredits();
        double needed = (target * (totalCredits() + next) - pts) / next;
        if (needed > 10) System.out.printf("Not reachable: you'd need %.2f (> 10).%n", needed);
        else System.out.printf("You need an average grade point of %.2f over the next %d credits.%n",
                Math.max(needed, 0), next);
    }

    // ---------- 6. DASHBOARD ----------
    static void dashboard() {
        System.out.println("\n===== DASHBOARD =====");
        LocalDate today = LocalDate.now();
        List<Deadline> pending = ws.deadlines.stream().filter(d -> !d.done())
                .sorted(Comparator.comparing(Deadline::due)).toList();
        System.out.println("Pending deadlines: " + pending.size());
        pending.stream().limit(3).forEach(d -> System.out.printf("  - %s: %s (%s, %d day(s))%n",
                d.subject(), d.title(), d.due(), ChronoUnit.DAYS.between(today, d.due())));
        List<String> low = ws.attendance.entrySet().stream()
                .filter(e -> pct(e.getValue()) < ATTENDANCE_MIN).map(Map.Entry::getKey).toList();
        System.out.println("Low attendance subjects: " + (low.isEmpty() ? "none" : String.join(", ", low)));
        long done = ws.topics.stream().filter(Topic::done).count();
        System.out.printf("Syllabus completed: %d/%d topics%n", done, ws.topics.size());
        System.out.printf("Notes stored: %d | CGPA: %.2f%n", ws.notes.size(), gpa(ws.courses));
    }
}
