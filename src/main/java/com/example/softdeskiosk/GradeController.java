package com.example.softdeskiosk;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.print.PrinterJob;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.control.ComboBox;
import javafx.geometry.Insets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class GradeController implements Initializable {
    private Kiosk kiosk;
    private final Map<String, StudentInfo> students = new HashMap<>();

    @FXML private ComboBox<String> yearLevelBox;
    @FXML private ComboBox<String> semesterBox;
    @FXML private ComboBox<String> purposeBox;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadStudentData();

        yearLevelBox.getItems().setAll("1st Year","2nd Year","3rd Year","4th Year","5th Year");
        semesterBox.getItems().setAll("1st Semester","2nd Semester");
        purposeBox.getItems().setAll(
                "Scholarship",
                "Student Discount Application",
                "Enrollment Verification",
                "Internship or OJT Requirements",
                "Employment Application",
                "Transferring to Another School"
        );
    }

    public void setKiosk(Kiosk kiosk) {
        this.kiosk = kiosk;
    }

    @FXML private void handleBackToMainMenu() {
        safeRun(() -> kiosk.showMainMenu());
    }

    @FXML private void handlePrintGradeLetter() {
        String key = kiosk.getCurrentStudentKey();
        if (key == null || !students.containsKey(key)) return;

        String yearLevel = yearLevelBox.getValue();
        String semester  = semesterBox.getValue();
        String purpose   = purposeBox.getValue();

        if (yearLevel == null || semester == null || purpose == null) {
            System.err.println("Select year level, semester, and purpose.");
            return;
        }

        StudentInfo info = students.get(key);
        VBox letter = buildGradeLetter(info, yearLevel, semester, purpose);

        PrinterJob job = PrinterJob.createPrinterJob();
        if (job != null && job.printPage(letter)) job.endJob();
    }

    private VBox buildGradeLetter(StudentInfo info,
                                  String yearLevel,
                                  String semester,
                                  String purpose) {
        String todayStr = LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM d, yyyy"));

        VBox box = new VBox(5);
        box.setPadding(new Insets(40));
        box.setStyle("-fx-background-color: white;");

        // Date & header
        box.getChildren().add(new Text(todayStr));
        box.getChildren().addAll(
                new Text("MS. RHODA MALABANAN"),
                new Text("Head, SFAU"),
                new Text("Rizal Technological University"),
                new Text("Dear Ma’am,"),
                new Text("")
        );

        // Body: two sentences
        Text bodyText = new Text(
                "I would like to request a grade slip for " +
                        yearLevel + ", " + semester + " to be submitted. " +
                        "As a requirement for my " + purpose + " application."
        );
        bodyText.setWrappingWidth(500);
        box.getChildren().add(bodyText);

        // Signature
        box.getChildren().addAll(
                new Text(""),
                new Text("Respectfully yours,"),
                new Text(""),
                new Text(info.name),
                new Text(info.key),
                new Text(info.program)
        );

        return box;
    }

    private void loadStudentData() {
        InputStream is = getClass().getResourceAsStream("/students.csv");
        if (is == null) return;
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            br.readLine();
            String line;
            while ((line = br.readLine()) != null) {
                String[] f = line.split(",");
                if (f.length >= 6) {
                    students.put(f[0].trim(), new StudentInfo(
                            f[0].trim(), f[1].trim(), f[2].trim(),
                            f[3].trim(), f[4].trim(), f[5].trim()
                    ));
                }
            }
        } catch (IOException ignored) {}
    }

    private void safeRun(Runnable r) { try { r.run(); } catch (Exception e) { e.printStackTrace(); } }

    private static class StudentInfo {
        final String key, name, gender, yearLevel, latestEnrollment, program;
        StudentInfo(String key, String name, String gender,
                    String yearLevel, String latestEnrollment, String program) {
            this.key = key; this.name = name; this.gender = gender;
            this.yearLevel = yearLevel; this.latestEnrollment = latestEnrollment;
            this.program = program;
        }
    }
}
