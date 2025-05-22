package com.example.softdeskiosk;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.print.PrinterJob;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class KioskController implements Initializable {
    public HBox topSelector;
    private Kiosk kiosk;
    @FXML private StackPane cardStack;
    @FXML private Button booksButton;
    @FXML private Button uniformsButton;
    @FXML private Button regiButton;
    @FXML private Button gradeButton;

    @FXML private VBox studentInfoPane;
    @FXML private VBox checkoutPane;
    @FXML private TableView<Item> cartTable;
    @FXML private TableColumn<Item, String> itemColumn;
    @FXML private TableColumn<Item, Integer> quantityColumn;
    @FXML private TableColumn<Item, Double> priceColumn;
    @FXML private TableColumn<Item, Void> editColumn;

    @FXML private TextField yearField;
    @FXML private TextField idField;
    @FXML private Label nameLabel, genderLabel, yearLevelLabel, latestEnrollmentLabel, programLabel;

    private boolean isPaid;

    private final ObservableList<Item> cartItems = FXCollections.observableArrayList();
    private final Map<String, StudentInfo> students = new HashMap<>();
    private final Map<String, Integer> stockMap = new HashMap<>();

    public void setKiosk(Kiosk kiosk) {
        this.kiosk = kiosk;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadStudentData();
        loadStockData();
        setupTableColumns();
        setupEditColumn();
        resetPanes();
    }

    // Load student info
    private void loadStudentData() {
        try (InputStream is = getClass().getResourceAsStream("/students.csv")) {
            if (is == null) return;
            try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
                br.readLine();
                String line;
                while ((line = br.readLine()) != null) {
                    String[] f = line.split(",", 6);
                    if (f.length < 6) continue;
                    String key = f[0].trim();
                    students.put(key, new StudentInfo(
                            f[1].trim(), f[2].trim(), f[3].trim(), f[4].trim(), f[5].trim()
                    ));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Load stock data
    private void loadStockData() {
        try (InputStream is = getClass().getResourceAsStream("/uniforms.csv")) {
            if (is == null) return;
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(is, StandardCharsets.UTF_8))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] parts = line.split(",", 5);
                    if (parts.length < 5) continue;
                    stockMap.put(parts[0].trim(), Integer.parseInt(parts[4].trim()));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Load cart with stock limits
    public void loadCartFromCSV() {
        cartItems.clear();
        try (BufferedReader br = Files.newBufferedReader(Paths.get("temp.csv"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] f = line.split(",", 3);
                String code  = f[0].trim();
                String name  = f[1].trim();
                double price = Double.parseDouble(f[2].trim());
                int maxStock = stockMap.getOrDefault(code, Integer.MAX_VALUE);

                Optional<Item> existing = cartItems.stream()
                        .filter(i -> i.getCode().equals(code))
                        .findFirst();
                if (existing.isPresent()) {
                    existing.get().incrementQuantity();
                } else {
                    cartItems.add(new Item(code, name, 1, price, maxStock));
                }
            }
        } catch (IOException ignored) {}
        updateCartTable();
    }

    public void updateCartTable() {
        cartTable.setItems(cartItems);
    }

    private void setupTableColumns() {
        cartTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        itemColumn.setCellValueFactory(new PropertyValueFactory<>("itemName"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        cartTable.setStyle("-fx-font-size: 18px;");
    }

    @FXML private void goToCheckout() {
        studentInfoPane.setVisible(false);
        studentInfoPane.setManaged(false);
        checkoutPane.setVisible(true);
        checkoutPane.setManaged(true);
        loadCartFromCSV();
    }

    @FXML private void showStudentInfoPane() {
        checkoutPane.setVisible(false);
        checkoutPane.setManaged(false);
        studentInfoPane.setVisible(true);
        studentInfoPane.setManaged(true);
    }

    private void resetPanes() {
        checkoutPane.setVisible(false);
        checkoutPane.setManaged(false);
        studentInfoPane.setVisible(true);
        studentInfoPane.setManaged(true);
        nameLabel.setText("Student Name: ");
        genderLabel.setText("Gender: ");
        yearLevelLabel.setText("Year Level: ");
        latestEnrollmentLabel.setText("Latest Enrollment: ");
        programLabel.setText("Program: ");
    }

    @FXML private void loadStudentInfo() {
        String key = yearField.getText().trim() + "-" + idField.getText().trim();
        StudentInfo info = students.get(key);
        if (info != null) {
            nameLabel.setText("Student Name: " + info.name);
            genderLabel.setText("Gender: " + info.gender);
            yearLevelLabel.setText("Year Level: " + info.yearLevel);
            latestEnrollmentLabel.setText("Latest Enrollment: " + info.latestEnrollment);
            programLabel.setText("Program: " + info.program);
            kiosk.setCurrentStudentKey(key);
        } else {
            resetPanes();
        }
    }

    public boolean hasStudent(String key) {
        return students.containsKey(key);
    }

    public void restoreStudentInfo(String key) {
        loadStudentInfo();
    }

    private void setupEditColumn() {
        editColumn.setCellFactory(col -> new TableCell<Item, Void>() {
            private final Button menuButton = new Button("⋮");
            private final ContextMenu contextMenu = new ContextMenu();
            {
                menuButton.getStyleClass().add("menu-button");
                MenuItem editQty = new MenuItem("Edit Quantity");
                MenuItem remove  = new MenuItem("Remove");
                editQty.setOnAction(e -> {
                    Item item = getTableRow().getItem();
                    if (item != null) showEditQuantityDialog(item);
                });
                remove.setOnAction(e -> {
                    Item item = getTableRow().getItem();
                    if (item != null) {
                        cartItems.remove(item);
                        updateTempCSV();
                        cartTable.refresh();
                    }
                });
                contextMenu.getItems().addAll(editQty, remove);
                menuButton.setOnAction(e -> contextMenu.show(menuButton, Side.BOTTOM, 0, 0));
            }
            @Override protected void updateItem(Void v, boolean empty) {
                super.updateItem(v, empty);
                setGraphic(empty ? null : menuButton);
            }
        });
    }

    private void showEditQuantityDialog(Item item) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        Label title    = new Label("Edit quantity for: " + item.getItemName());
        title.getStyleClass().add("dialog-title");
        Label qtyLabel = new Label(String.valueOf(item.getQuantity()));
        qtyLabel.getStyleClass().add("dialog-quantity");
        Button minus   = new Button("–");
        Button plus    = new Button("+");
        minus.getStyleClass().add("quantity-button");
        plus.getStyleClass().add("quantity-button");
        plus.setDisable(item.getQuantity() >= item.getMaxStock());
        minus.setOnAction(e -> {
            int q = item.getQuantity();
            if (q > 1) {
                item.setQuantity(q - 1);
                qtyLabel.setText(String.valueOf(item.getQuantity()));
                if (item.getQuantity() < item.getMaxStock()) plus.setDisable(false);
            }
        });
        plus.setOnAction(e -> {
            int q = item.getQuantity();
            if (q < item.getMaxStock()) {
                item.setQuantity(q + 1);
                qtyLabel.setText(String.valueOf(item.getQuantity()));
                if (item.getQuantity() >= item.getMaxStock()) plus.setDisable(true);
            }
        });
        Button ok = new Button("OK");
        ok.getStyleClass().addAll("button", "dialog-ok-button");
        ok.setOnAction(e -> {
            updateTempCSV();
            cartTable.refresh();
            dialog.close();
        });
        HBox controls = new HBox(10, minus, qtyLabel, plus);
        controls.setAlignment(Pos.CENTER);
        VBox layout = new VBox(20, title, controls, ok);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);
        layout.getStyleClass().add("dialog-root");
        Scene scene = new Scene(layout);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        dialog.setScene(scene);
        dialog.setTitle("Edit Quantity");
        dialog.showAndWait();
    }

    private void updateTempCSV() {
        try (BufferedWriter w = Files.newBufferedWriter(Paths.get("temp.csv"))) {
            for (Item it : cartItems) {
                for (int i = 0; i < it.getQuantity(); i++) {
                    w.write(it.getCode() + "," + it.getItemName() + "," + it.getPrice());
                    w.newLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML private VBox leftContainer;
    @FXML private HBox navButtons;








    // Add these FXMl injections at the top of your controller
    @FXML private HBox baoContainer;
    @FXML private VBox registrarContainer;
    @FXML private VBox misoContainer;

    @FXML
    private void showRegistrar() {
        hideAllContainers();
        registrarContainer.setVisible(true);
        registrarContainer.setManaged(true);
        backButton.setVisible(true);
        backButton.setManaged(true);
    }

    @FXML
    private void showBao() {
        hideAllContainers();
        baoContainer.setVisible(true);
        baoContainer.setManaged(true);
        backButton.setVisible(true);
        backButton.setManaged(true);
    }

    @FXML
    private void showMiso() {
        hideAllContainers();
        misoContainer.setVisible(true);
        misoContainer.setManaged(true);
        backButton.setVisible(true);
        backButton.setManaged(true);
    }

    @FXML
    private void handleBackToTop() {
        hideAllContainers();
        backButton.setVisible(false);
        backButton.setManaged(false);
    }

    private void hideAllContainers() {
        baoContainer.setVisible(false);
        baoContainer.setManaged(false);
        registrarContainer.setVisible(false);
        registrarContainer.setManaged(false);
        misoContainer.setVisible(false);
        misoContainer.setManaged(false);
    }
    @FXML private Button backButton;





    // Payment and printing
    @FXML private void proceedToOnlinePayment() {
        Stage qrStage = new Stage();
        qrStage.initModality(Modality.APPLICATION_MODAL);
        qrStage.initOwner(yearField.getScene().getWindow());
        qrStage.setTitle("Scan to Pay");
        ImageView qrView = new ImageView(new Image(getClass().getResourceAsStream("/qr_example.png")));
        qrView.setPreserveRatio(true);
        qrView.setFitWidth(200);
        Button back = new Button("Back");
        back.setMinWidth(100);
        back.setOnAction(e -> qrStage.close());
        Button validate = new Button("Validate Payment");
        validate.setMinWidth(300);
        HBox buttons = new HBox(20, back, validate);
        buttons.setAlignment(Pos.CENTER);
        VBox layout = new VBox(20, qrView, buttons);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));
        Scene scene = new Scene(layout, 540, 480);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        validate.setOnAction(e -> {
            isPaid = true;
            layout.getChildren().add(new Label("Printing paid orderslip"));
            handlePrintSlip();
            try { Files.deleteIfExists(Paths.get("temp.csv")); } catch (IOException ex) { ex.printStackTrace(); }
            resetPanes();
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

    private VBox buildPrintSlip() {
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setVgap(8);
        grid.setHgap(20);
        ColumnConstraints c1 = new ColumnConstraints(); c1.setPercentWidth(60);
        ColumnConstraints c2 = new ColumnConstraints(); c2.setPercentWidth(20); c2.setHalignment(HPos.CENTER);
        ColumnConstraints c3 = new ColumnConstraints(); c3.setPercentWidth(20); c3.setHalignment(HPos.RIGHT);
        grid.getColumnConstraints().addAll(c1, c2, c3);
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
        VBox printNode = buildPrintSlip();
        PrinterJob job = PrinterJob.createPrinterJob();
        if (job == null) {
            System.err.println("No printers available");
            return;
        }
        boolean success = job.printPage(printNode);
        if (success) job.endJob();
    }

    @FXML private void goToBooks()    { safeRun(() -> kiosk.showBookScene()); }
    @FXML private void goToUniforms(){ safeRun(() -> kiosk.showUniformScene()); }
    @FXML private void goToRegiform(){ safeRun(() -> kiosk.showRegiformScene()); }
    @FXML private void goToGrade()   { safeRun(() -> kiosk.showGradeScene()); }
    @FXML private void restartKiosk(){ try { Files.deleteIfExists(Paths.get("temp.csv")); } catch (IOException e) { e.printStackTrace(); } cartItems.clear(); updateCartTable(); resetPanes(); }
    @FXML private void handleBackToMainMenu(){ safeRun(() -> kiosk.showMainMenu()); }

    private void safeRun(Runnable r) { try { r.run(); } catch (Exception e) { e.printStackTrace(); } }

    public static class Item {
        private final String code;
        private final String itemName;
        private int quantity;
        private final double price;
        private final int maxStock;
        public Item(String code, String name, int qty, double price, int maxStock) {
            this.code     = code;
            this.itemName = name;
            this.quantity = qty;
            this.price    = price;
            this.maxStock = maxStock;
        }
        public String getCode()      { return code; }
        public String getItemName()  { return itemName; }
        public Integer getQuantity() { return quantity; }
        public Double getPrice()     { return price; }
        public int getMaxStock()     { return maxStock; }
        public void incrementQuantity(){ if(quantity<maxStock) quantity++; }
        public void setQuantity(int q){ this.quantity = Math.min(q,maxStock); }
    }

    private static class StudentInfo {
        String name, gender, yearLevel, latestEnrollment, program;
        StudentInfo(String n, String g, String y, String le, String p) {
            name=n;gender=g;yearLevel=y;latestEnrollment=le;program=p;
        }
    }
}
