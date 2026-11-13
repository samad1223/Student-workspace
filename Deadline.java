import java.time.LocalDate;

public record Deadline(String subject, String title, LocalDate due, boolean done) {}
