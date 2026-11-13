import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Consumer;

/** Holds all in-memory collections and persists them to plain text files in ./data */
public class Workspace {
    static final Path DIR = Paths.get("data");

    final List<Note> notes = new ArrayList<>();
    final List<Deadline> deadlines = new ArrayList<>();
    final List<Topic> topics = new ArrayList<>();
    final List<Course> courses = new ArrayList<>();
    final Map<String, int[]> attendance = new LinkedHashMap<>(); // subject -> {attended, total}

    void load() {
        read("notes.txt", p -> notes.add(new Note(p[0], p[1], p[2])));
        read("deadlines.txt", p -> deadlines.add(
                new Deadline(p[0], p[1], LocalDate.parse(p[2]), Boolean.parseBoolean(p[3]))));
        read("syllabus.txt", p -> topics.add(new Topic(p[0], p[1], Boolean.parseBoolean(p[2]))));
        read("courses.txt", p -> courses.add(new Course(
                Integer.parseInt(p[0]), p[1], Integer.parseInt(p[2]), Double.parseDouble(p[3]))));
        read("attendance.txt", p -> attendance.put(p[0],
                new int[]{Integer.parseInt(p[1]), Integer.parseInt(p[2])}));
    }

    void save() {
        try {
            Files.createDirectories(DIR);
            write("notes.txt", notes.stream()
                    .map(n -> String.join("|", n.subject(), n.title(), n.body())).toList());
            write("deadlines.txt", deadlines.stream()
                    .map(d -> String.join("|", d.subject(), d.title(), d.due().toString(),
                            String.valueOf(d.done()))).toList());
            write("syllabus.txt", topics.stream()
                    .map(t -> String.join("|", t.subject(), t.name(), String.valueOf(t.done()))).toList());
            write("courses.txt", courses.stream()
                    .map(c -> c.semester() + "|" + c.name() + "|" + c.credits() + "|" + c.gradePoint()).toList());
            write("attendance.txt", attendance.entrySet().stream()
                    .map(e -> e.getKey() + "|" + e.getValue()[0] + "|" + e.getValue()[1]).toList());
        } catch (IOException e) {
            System.out.println("Error saving data: " + e.getMessage());
        }
    }

    private void read(String file, Consumer<String[]> rowHandler) {
        Path p = DIR.resolve(file);
        if (!Files.exists(p)) return;
        try {
            for (String line : Files.readAllLines(p)) {
                if (line.isBlank()) continue;
                try {
                    rowHandler.accept(line.split("\\|", -1));
                } catch (RuntimeException e) {
                    System.out.println("Skipped a corrupt line in " + file);
                }
            }
        } catch (IOException e) {
            System.out.println("Could not read " + file + ": " + e.getMessage());
        }
    }

    private void write(String file, List<String> lines) throws IOException {
        Files.write(DIR.resolve(file), lines);
    }
}
