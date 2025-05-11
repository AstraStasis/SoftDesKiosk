package com.example.softdeskiosk;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Optional;

public class KioskController implements Initializable {
    private Kiosk kiosk;

    // --- panes & table for checkout ---
    @FXML private VBox   studentInfoPane;
    @FXML private VBox   checkoutPane;
    @FXML private TableView<Item> cartTable;
    @FXML private TableColumn<Item, String>  itemColumn;
    @FXML private TableColumn<Item, Integer> quantityColumn;
    @FXML private TableColumn<Item, Double>  priceColumn;
    private final ObservableList<Item> cartItems = FXCollections.observableArrayList();

    // --- student lookup inputs & labels ---
    @FXML private TextField yearField;
    @FXML private TextField idField;

    @FXML private Label nameLabel;
    @FXML private Label genderLabel;
    @FXML private Label yearLevelLabel;
    @FXML private Label latestEnrollmentLabel;
    @FXML private Label programLabel;

    // --- student data map: "YYYY-ID" -> StudentInfo ---
    private final Map<String, StudentInfo> students = new HashMap<>();

    public void setKiosk(Kiosk kiosk) {
        this.kiosk = kiosk;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadStudentData();

        // Initialize labels blank but keep prefixes
        nameLabel.setText("Student Name: ");
        genderLabel.setText("Gender: ");
        yearLevelLabel.setText("Year Level: ");
        latestEnrollmentLabel.setText("Latest Enrollment: ");
        programLabel.setText("Program: ");

        // hide checkout pane initially
        checkoutPane.setVisible(false);
        checkoutPane.setManaged(false);
        updateCartTable();
    }

    /** Load student data from classpath resource students.csv */
    private void loadStudentData() {
        InputStream is = getClass().getResourceAsStream("/students.csv");
        if (is == null) {
            System.err.println("Error: students.csv not found in resources");
            return;
        }
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            String line = br.readLine(); // skip header
            while ((line = br.readLine()) != null) {
                String[] f = line.split(",");
                if (f.length >= 6) {
                    String key = f[0].trim();
                    StudentInfo info = new StudentInfo(
                            f[1].trim(), // name
                            f[2].trim(), // gender
                            f[3].trim(), // year level
                            f[4].trim(), // latest enrollment
                            f[5].trim()  // program
                    );
                    students.put(key, info);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading students.csv: " + e.getMessage());
        }
    }

    /** Load temp.csv into cartItems */
    public void loadCartFromCSV() {
        cartItems.clear();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(
                        Files.newInputStream(Paths.get("temp.csv"))))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] f = line.split(",", 3);
                String name = f[1].trim();
                double price = Double.parseDouble(f[2].trim());
                boolean found = false;
                for (Item it : cartItems) {
                    if (it.getItemName().equals(name)) {
                        it.incrementQuantity();
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    cartItems.add(new Item(name, 1, price));
                }
            }
        } catch (IOException ignored) { }
    }

    /** Wire up the table columns and items */
    public void updateCartTable() {
        itemColumn.setCellValueFactory(new PropertyValueFactory<>("itemName"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        cartTable.setItems(cartItems);
    }

    /*--- Navigation buttons ---*/
    @FXML private void goToBooks()     { safeRun(() -> kiosk.showBookScene()); }
    @FXML private void goToUniforms()  { safeRun(() -> kiosk.showUniformScene()); }
    @FXML private void goToRegiform()  { safeRun(() -> kiosk.showRegiformScene()); }
    @FXML private void goToGrade()     { safeRun(() -> kiosk.showGradeScene()); }

    @FXML private void restartKiosk() {
        try { Files.deleteIfExists(Paths.get("temp.csv")); }
        catch (IOException e) { e.printStackTrace(); }
        loadCartFromCSV();
        updateCartTable();
    }

    /** Show the checkout pane */
    @FXML private void goToCheckout() {
        studentInfoPane.setVisible(false);
        studentInfoPane.setManaged(false);
        checkoutPane.setVisible(true);
        checkoutPane.setManaged(true);
        updateCartTable();
    }

    /** Back to student info card */
    @FXML private void showStudentInfoPane() {
        checkoutPane.setVisible(false);
        checkoutPane.setManaged(false);
        studentInfoPane.setVisible(true);
        studentInfoPane.setManaged(true);
    }

    /** Print-order button */
    @FXML private void handlePrintSlip() {
        System.out.println("Printing order slip...");
    }

    /** Lookup student by entered number and display */
    @FXML private void loadStudentInfo() {
        String year = yearField.getText().trim();
        String id   = idField.getText().trim();

        // Validate input
        if (year.isEmpty() || id.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING,
                    "Student number is required.", ButtonType.OK);
            alert.setHeaderText("Missing Input");
            alert.showAndWait();
            return;
        }

        String key  = year + "-" + id;
        StudentInfo info = students.get(key);
        if (info != null) {
            nameLabel.setText("Student Name: " + info.name);
            genderLabel.setText("Gender: " + info.gender);
            yearLevelLabel.setText("Year Level: " + info.yearLevel);
            latestEnrollmentLabel.setText("Latest Enrollment: " + info.latestEnrollment);
            programLabel.setText("Program: " + info.program);
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR,
                    "No student found for " + key, ButtonType.OK);
            alert.setHeaderText("Invalid Student Number");
            alert.showAndWait();

            // Keep prefixes and clear details
            nameLabel.setText("Student Name: ");
            genderLabel.setText("Gender: ");
            yearLevelLabel.setText("Year Level: ");
            latestEnrollmentLabel.setText("Latest Enrollment: ");
            programLabel.setText("Program: ");
        }
        // Show info pane and hide checkout
        studentInfoPane.setVisible(true);
        studentInfoPane.setManaged(true);
        checkoutPane.setVisible(false);
        checkoutPane.setManaged(false);
    }

    private void safeRun(Runnable r) { try { r.run(); } catch (Exception e) { e.printStackTrace(); }}

    /** Simple cart‐item model */
    public static class Item {
        private final String itemName;
        private int quantity;
        private final double price;
        public Item(String name, int qty, double pr) {
            this.itemName = name;
            this.quantity = qty;
            this.price    = pr;
        }
        public String getItemName()  { return itemName; }
        public Integer getQuantity() { return quantity; }
        public Double getPrice()     { return price; }
        public void incrementQuantity() { quantity++; }
    }

    /** Helper class for student info */
    private static class StudentInfo {
        String name, gender, yearLevel, latestEnrollment, program;
        StudentInfo(String n, String g, String y, String le, String p) {
            name = n; gender = g; yearLevel = y; latestEnrollment = le; program = p;
        }
    }
}