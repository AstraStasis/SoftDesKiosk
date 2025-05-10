package com.example.softdeskiosk;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;

public class UniformController {
    @FXML private TextField searchBar;
    @FXML private FlowPane productGrid;

    private Kiosk kiosk;
    private final ArrayList<VBox> uniformBoxes = new ArrayList<>();

    public void setKiosk(Kiosk kiosk) {
        this.kiosk = kiosk;
    }

    @FXML
    public void initialize() {
        // Load uniform list from CSV
        try (InputStream is = getClass().getResourceAsStream("/uniforms.csv")) {
            if (is != null) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",", 3);
                    if (parts.length < 3) continue;
                    VBox box = createProductBox(parts[0], parts[1], parts[2]);
                    productGrid.getChildren().add(box);
                    uniformBoxes.add(box);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Search filter
        searchBar.textProperty().addListener((obs, oldVal, newVal) -> {
            productGrid.getChildren().clear();
            for (VBox b : uniformBoxes) {
                Text t = (Text) b.getChildren().get(1);
                if (t.getText().toLowerCase().contains(newVal.toLowerCase())) {
                    productGrid.getChildren().add(b);
                }
            }
        });
    }

    private VBox createProductBox(String id, String name, String price) {
        VBox box = new VBox(10);
        box.setStyle("-fx-border-color:#ccc; -fx-background-color:#f9f9f9; -fx-border-radius:10; -fx-background-radius:10;");
        box.setPadding(new javafx.geometry.Insets(15));
        box.setPrefSize(240, 270);
        box.setAlignment(javafx.geometry.Pos.CENTER);

        // Image
        InputStream imgIs = getClass().getResourceAsStream("/uniform_images/" + id + ".png");
        Image img = new Image(imgIs != null ? imgIs : getClass().getResourceAsStream("/placeholder_img.png"));
        ImageView iv = new ImageView(img);
        iv.setFitHeight(100);
        iv.setPreserveRatio(true);

        // Name & Price
        Text tName = new Text(name);
        Text tPrice = new Text("₱" + price);

        // Order button
        Button btn = new Button("Order");
        btn.setMaxWidth(160);
        btn.setOnAction(e -> {
            // Append to temp.csv
            try {
                Path p = Paths.get("temp.csv");
                try (BufferedWriter w = Files.newBufferedWriter(p, StandardCharsets.UTF_8,
                        StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
                    w.write(id + "," + name + "," + price);
                    w.newLine();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            // Update button UI
            btn.setText("Ordered");
            btn.setDisable(true);
            // Return to main menu
            try {
                kiosk.showMainMenu();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        box.getChildren().addAll(iv, tName, tPrice, btn);
        return box;
    }

    @FXML
    private void handleBackToMainMenu() {
        try {
            kiosk.showMainMenu();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
