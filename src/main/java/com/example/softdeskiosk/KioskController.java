package com.example.softdeskiosk;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.print.PrinterJob;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import javafx.geometry.Pos;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class KioskController implements Initializable {
    private Kiosk kiosk;

    @FXML private VBox studentInfoPane;
    @FXML private VBox checkoutPane;
    @FXML private TableView<Item> cartTable;
    @FXML private TableColumn<Item, String> itemColumn;
    @FXML private TableColumn<Item, Integer> quantityColumn;
    @FXML private TableColumn<Item, Double> priceColumn;
    @FXML private TableColumn<Item, Void> editColumn;
    private final ObservableList<Item> cartItems = FXCollections.observableArrayList();

    @FXML private TextField yearField;
    @FXML private TextField idField;

    @FXML private Label nameLabel;
    private boolean isPaid;

    @FXML private Label genderLabel;
    @FXML private Label yearLevelLabel;
    @FXML private Label latestEnrollmentLabel;
    @FXML private Label programLabel;

    private final Map<String, StudentInfo> students = new HashMap<>();
    private String menuButtonIconPath = null; // Path to icon image for menu button

    public void setKiosk(Kiosk kiosk) {
        this.kiosk = kiosk;
    }
    @FXML private void proceedToOnlinePayment() {
        Stage qrStage = new Stage();
        qrStage.initModality(Modality.APPLICATION_MODAL);
        qrStage.initOwner(((Node) yearField).getScene().getWindow());
        qrStage.setTitle("Scan to Pay");
        ImageView qrView = new ImageView(new Image(getClass().getResourceAsStream("/qr_example.png")));
        qrView.setPreserveRatio(true);
        qrView.setFitWidth(200);
        Button back = new Button("Back");
        back.getStyleClass().add("payment-button");
        back.setMinWidth(100);
        back.setOnAction(e -> qrStage.close());
        Button validate = new Button("Validate Payment");
        validate.getStyleClass().add("payment-button");
        validate.setMinWidth(300);
        HBox buttons = new HBox(20, back, validate);
        buttons.setAlignment(Pos.CENTER);
        VBox layout = new VBox(20, qrView, buttons);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));
        layout.getStyleClass().add("root");
        Scene scene = new Scene(layout, 540, 480);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        validate.setOnAction(e -> {
            isPaid = true;
            Label msg = new Label("Printing paid orderslip");
            msg.getStyleClass().add("card-text");
            layout.getChildren().add(msg);
            handlePrintSlip();
            try { Files.deleteIfExists(Paths.get("temp.csv")); } catch (IOException ex) { ex.printStackTrace(); }
            yearField.clear(); idField.clear();
            nameLabel.setText("Student Name: ");
            genderLabel.setText("Gender: ");
            yearLevelLabel.setText("Year Level: ");
            latestEnrollmentLabel.setText("Latest Enrollment: ");
            programLabel.setText("Program: ");
            qrStage.close();
        });
        qrStage.setScene(scene);
        qrStage.centerOnScreen();
        qrStage.showAndWait();
    }




    @FXML private void payAtCounter() {
        isPaid = false;
        handlePrintSlip();
    }

    @Override

    public void initialize(URL location, ResourceBundle resources) {
        loadStudentData();
        cartTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        itemColumn.setPrefWidth(200);
        itemColumn.setMinWidth(150);
        quantityColumn.setPrefWidth(50);
        quantityColumn.setMinWidth(50);
        priceColumn.setMinWidth(60);
        priceColumn.setPrefWidth(80);
        priceColumn.setMaxWidth(80);
        editColumn.setMinWidth(50);
        editColumn.setPrefWidth(50);
        editColumn.setMaxWidth(50);
        updateCartTable();
        setupEditColumn();
        nameLabel.setText("Student Name: ");
        genderLabel.setText("Gender: ");
        yearLevelLabel.setText("Year Level: ");
        latestEnrollmentLabel.setText("Latest Enrollment: ");
        programLabel.setText("Program: ");
        checkoutPane.setVisible(false);
        checkoutPane.setManaged(false);
        studentInfoPane.setVisible(true);
        studentInfoPane.setManaged(true);
        cartTable.setStyle("-fx-font-size: 18px;");
    }


    private void loadStudentData() {
        InputStream is = getClass().getResourceAsStream("/students.csv");
        if (is == null) {
            System.err.println("Error: students.csv not found in resources");
            return;
        }
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            br.readLine(); // skip header
            String line;
            while ((line = br.readLine()) != null) {
                String[] f = line.split(",");
                if (f.length >= 6) {
                    students.put(f[0].trim(), new StudentInfo(
                            f[1].trim(), f[2].trim(), f[3].trim(), f[4].trim(), f[5].trim()
                    ));
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading students.csv: " + e.getMessage());
        }
    }

    public void loadCartFromCSV() {
        cartItems.clear();
        try (BufferedReader br = Files.newBufferedReader(Paths.get("temp.csv"))) {
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
                if (!found) cartItems.add(new Item(name, 1, price));
            }
        } catch (IOException ignored) { }
    }

    public void updateCartTable() {
        itemColumn.setCellValueFactory(new PropertyValueFactory<>("itemName"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        cartTable.setItems(cartItems);
    }

    @FXML private void goToBooks()    { safeRun(() -> kiosk.showBookScene()); }
    @FXML private void goToUniforms(){ safeRun(() -> kiosk.showUniformScene()); }
    @FXML private void goToRegiform(){ safeRun(() -> kiosk.showRegiformScene()); }
    @FXML private void goToGrade()   { safeRun(() -> kiosk.showGradeScene()); }

    @FXML private void restartKiosk() {
        try { Files.deleteIfExists(Paths.get("temp.csv")); }
        catch (IOException e) { e.printStackTrace(); }
        loadCartFromCSV();
        updateCartTable();
    }

    @FXML private void goToCheckout() {
        studentInfoPane.setVisible(false);
        studentInfoPane.setManaged(false);
        checkoutPane.setVisible(true);
        checkoutPane.setManaged(true);
        updateCartTable();
    }

    @FXML private void showStudentInfoPane() {
        checkoutPane.setVisible(false);
        checkoutPane.setManaged(false);
        studentInfoPane.setVisible(true);
        studentInfoPane.setManaged(true);
    }

    /** Build a print‐ready slip from current student + cart data */
    private VBox buildPrintSlip() {
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setVgap(8);
        grid.setHgap(20);
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(60);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(20);
        ColumnConstraints col3 = new ColumnConstraints();
        col3.setPercentWidth(20);
        col2.setHalignment(HPos.CENTER);
        col3.setHalignment(HPos.RIGHT);
        grid.getColumnConstraints().addAll(col1, col2, col3);

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        grid.add(new Label("Your Order Slip"), 0, 0, 3, 1);
        grid.add(new Label("Date: " + LocalDateTime.now().format(fmt)), 0, 1, 3, 1);

        grid.add(new Label(nameLabel.getText()), 0, 2, 3, 1);
        grid.add(new Label(genderLabel.getText()), 0, 3, 3, 1);
        grid.add(new Label(yearLevelLabel.getText()), 0, 4, 3, 1);

        grid.add(new Label("Item"), 0, 6);
        grid.add(new Label("Qty"), 1, 6);
        grid.add(new Label("Price"), 2, 6);

        int row = 7;
        for (Item it : cartItems) {
            grid.add(new Label(it.getItemName()), 0, row);
            grid.add(new Label(it.getQuantity().toString()), 1, row);
            grid.add(new Label(String.format("₱%.2f", it.getPrice())), 2, row++);
        }

        double total = cartItems.stream().mapToDouble(i -> i.getQuantity() * i.getPrice()).sum();
        grid.add(new Label("Total:"), 1, row);
        grid.add(new Label(String.format("₱%.2f", total)), 2, row);
        grid.add(new Label("Status:" + (isPaid ? " Paid" : " Unpaid")), 1, ++row, 2, 1);

        VBox slip = new VBox(grid);
        slip.setStyle("-fx-background-color:white; -fx-font-size:14px;");
        return slip;
    }


    @FXML private void handlePrintSlip() {
        // 1) build slip node
        VBox printNode = buildPrintSlip();

        // 2) send to printer
        PrinterJob job = PrinterJob.createPrinterJob();
        if (job == null) {
            System.err.println("No printers available");
            return;
        }
        boolean success = job.printPage(printNode);
        if (success) {
            job.endJob();
            System.out.println("Printed successfully");
        } else {
            System.err.println("Print failed");
        }
    }

    @FXML private void loadStudentInfo() {
        String year = yearField.getText().trim();
        String id   = idField.getText().trim();
        String key  = year + "-" + id;

        StudentInfo info = students.get(key);
        if (info != null) {
            displayStudentInfo(info);
            kiosk.setCurrentStudentKey(key);
        } else {
            // clear on bad lookup
            nameLabel.setText("Student Name: ");
            genderLabel.setText("Gender: ");
            yearLevelLabel.setText("Year Level: ");
            latestEnrollmentLabel.setText("Latest Enrollment: ");
            programLabel.setText("Program: ");
        }

        studentInfoPane.setVisible(true);
        studentInfoPane.setManaged(true);
        checkoutPane.setVisible(false);
        checkoutPane.setManaged(false);
    }

    public boolean hasStudent(String key) {
        return students.containsKey(key);
    }

    public void restoreStudentInfo(String key) {
        StudentInfo info = students.get(key);
        if (info != null) {
            displayStudentInfo(info);
            studentInfoPane.setVisible(true);
            studentInfoPane.setManaged(true);
            checkoutPane.setVisible(false);
            checkoutPane.setManaged(false);
        }
    }

    private void displayStudentInfo(StudentInfo info) {
        nameLabel.setText("Student Name: " + info.name);
        genderLabel.setText("Gender: " + info.gender);
        yearLevelLabel.setText("Year Level: " + info.yearLevel);
        latestEnrollmentLabel.setText("Latest Enrollment: " + info.latestEnrollment);
        programLabel.setText("Program: " + info.program);
    }

    private void safeRun(Runnable r) {
        try { r.run(); } catch (Exception e) { e.printStackTrace(); }
    }

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

    private static class StudentInfo {
        String name, gender, yearLevel, latestEnrollment, program;
        StudentInfo(String n, String g, String y, String le, String p) {
            name = n; gender = g; yearLevel = y; latestEnrollment = le; program = p;
        }
    }

    private void setupEditColumn() {
        editColumn.setCellFactory(col -> new javafx.scene.control.TableCell<Item, Void>() {
            private final javafx.scene.control.Button menuButton = new javafx.scene.control.Button();
            private final ContextMenu contextMenu = new ContextMenu();
            private final MenuItem editItem = new MenuItem("Edit Quantity");
            private final MenuItem removeItem = new MenuItem("Remove");
            {
                // Style the menu button to be a couple of pixels larger
                menuButton.getStyleClass().add("menu-button");
                // Set icon if provided
                if (menuButtonIconPath != null) {
                    try {
                        Image img = new Image(menuButtonIconPath);
                        ImageView iv = new ImageView(img);
                        iv.setFitWidth(20);
                        iv.setFitHeight(20);
                        menuButton.setGraphic(iv);
                        menuButton.setText("");
                    } catch (Exception e) {
                        menuButton.setText("⋮");
                    }
                } else {
                    menuButton.setText("⋮");
                }
                editItem.setOnAction(e -> {
                    Item item = getTableView().getItems().get(getIndex());
                    handleEditQuantity(item);
                });
                removeItem.setOnAction(e -> {
                    Item item = getTableView().getItems().get(getIndex());
                    handleRemoveItem(item);
                });
                contextMenu.getItems().addAll(editItem, removeItem);
                menuButton.setOnAction(e -> {
                    contextMenu.show(menuButton, javafx.geometry.Side.BOTTOM, 0, 0);
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    // Re-apply icon if needed
                    if (menuButtonIconPath != null) {
                        try {
                            Image img = new Image(menuButtonIconPath);
                            ImageView iv = new ImageView(img);
                            iv.setFitWidth(20);
                            iv.setFitHeight(20);
                            menuButton.setGraphic(iv);
                            menuButton.setText("");
                        } catch (Exception e) {
                            menuButton.setText("⋮");
                        }
                    } else {
                        menuButton.setText("⋮");
                        menuButton.setGraphic(null);
                    }
                    setGraphic(menuButton);
                }
            }
        });
    }

    private void handleEditQuantity(Item item) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        Label title = new Label("Edit quantity for: " + item.getItemName());
        title.getStyleClass().add("card-text");
        Label qtyLabel = new Label(String.valueOf(item.getQuantity()));
        qtyLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold;");
        Button minus = new Button("–");
        Button plus  = new Button("+");
        minus.getStyleClass().add("button");
        plus.getStyleClass().add("button");
        minus.setOnAction(e -> {
            int q = Integer.parseInt(qtyLabel.getText());
            if (q > 1) qtyLabel.setText(String.valueOf(q - 1));
        });
        plus.setOnAction(e -> {
            int q = Integer.parseInt(qtyLabel.getText());
            qtyLabel.setText(String.valueOf(q + 1));
        });
        Button ok = new Button("OK");
        ok.getStyleClass().add("button");
        ok.setOnAction(e -> {
            item.quantity = Integer.parseInt(qtyLabel.getText());
            updateTempCSV();
            cartTable.refresh();
            dialog.close();
        });
        HBox controls = new HBox(20, minus, qtyLabel, plus);
        controls.setAlignment(Pos.CENTER);
        VBox layout = new VBox(30, title, controls, ok);
        layout.setPadding(new Insets(30));
        layout.setAlignment(Pos.CENTER);
        Scene scene = new Scene(layout, 400, 250);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        dialog.setScene(scene);
        dialog.setTitle("Edit Quantity");
        dialog.showAndWait();
    }



    private void handleRemoveItem(Item item) {
        cartItems.remove(item);
        updateCartTable();
        updateTempCSV();
        cartTable.refresh();

    }

    private void updateTempCSV() {
        try (java.io.BufferedWriter writer = java.nio.file.Files.newBufferedWriter(java.nio.file.Paths.get("temp.csv"))) {
            for (Item it : cartItems) {
                for (int i = 0; i < it.getQuantity(); i++) {
                    writer.write("," + it.getItemName() + "," + it.getPrice());
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setMenuButtonIconPath(String path) {
        this.menuButtonIconPath = path;
    }
}
