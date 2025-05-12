package com.example.softdeskiosk;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UniformController {

    @FXML private TextField searchBar;
    @FXML private FlowPane productGrid;

    private Kiosk kiosk;
    private final Map<String, List<Uniform>> uniformMap = new HashMap<>();

    public void setKiosk(Kiosk kiosk) {
        this.kiosk = kiosk;
    }

    @FXML
    public void initialize() {
        loadUniforms();
        showClassifications();

        // Center items in the FlowPane
        productGrid.setAlignment(Pos.CENTER);

        // Search filter for classifications
        searchBar.textProperty().addListener((obs, oldVal, newVal) -> {
            productGrid.getChildren().clear();
            uniformMap.keySet().stream()
                    .filter(key -> key.toLowerCase().contains(newVal.toLowerCase()))
                    .forEach(key -> productGrid.getChildren().add(createClassificationBox(key)));
        });
    }

    private void loadUniforms() {
        try (InputStream is = getClass().getResourceAsStream("/uniforms.csv")) {
            if (is != null) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",", 3);
                    if (parts.length < 3) continue;

                    Uniform u = new Uniform(parts[0], parts[1], parts[2]);
                    uniformMap.computeIfAbsent(u.getClassification(), k -> new ArrayList<>()).add(u);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showClassifications() {
        productGrid.getChildren().clear();
        for (String classification : uniformMap.keySet()) {
            productGrid.getChildren().add(createClassificationBox(classification));
        }
    }

    private VBox createClassificationBox(String classification) {
        VBox box = new VBox(10);
        box.setStyle("-fx-border-color:#ccc; -fx-background-color:#f0f0f0; -fx-border-radius:10; -fx-background-radius:10;");
        box.setPadding(new javafx.geometry.Insets(15));
        box.setPrefSize(200, 260);  // increased height for better visibility
        box.setAlignment(Pos.CENTER);

        // Hover animation (scale up/down)
        ScaleTransition hoverEnter = new ScaleTransition(Duration.millis(200), box);
        hoverEnter.setToX(1.05);
        hoverEnter.setToY(1.05);
        ScaleTransition hoverExit = new ScaleTransition(Duration.millis(200), box);
        hoverExit.setToX(1.0);
        hoverExit.setToY(1.0);
        box.setOnMouseEntered(e -> hoverEnter.playFromStart());
        box.setOnMouseExited(e -> hoverExit.playFromStart());

        String imgPath = "/uniform_images/" + classification.replaceAll("\\s+", "_") + ".png";
        InputStream imgIs = getClass().getResourceAsStream(imgPath);
        Image img = new Image(imgIs != null ? imgIs : getClass().getResourceAsStream("/placeholder_img.png"));
        ImageView iv = new ImageView(img);
        iv.setFitHeight(120);
        iv.setPreserveRatio(true);

        Text name = new Text(classification);

        // Click to fade out then show sizes
        box.setOnMouseClicked(e -> {
            FadeTransition ft = new FadeTransition(Duration.millis(300), productGrid);
            ft.setFromValue(1.0);
            ft.setToValue(0.0);
            ft.setOnFinished(evt -> showSizesFor(classification));
            ft.play();
        });

        box.getChildren().addAll(iv, name);
        return box;
    }

    private void showSizesFor(String classification) {
        productGrid.getChildren().clear();
        List<Uniform> options = uniformMap.get(classification);
        if (options != null) {
            for (Uniform u : options) {
                productGrid.getChildren().add(createUniformBox(u));
            }
        }
        FadeTransition ft = new FadeTransition(Duration.millis(300), productGrid);
        ft.setFromValue(0.0);
        ft.setToValue(1.0);
        ft.play();
    }

    private VBox createUniformBox(Uniform u) {
        VBox box = new VBox(10);
        box.setStyle("-fx-border-color:#ccc; -fx-background-color:#f9f9f9; -fx-border-radius:10; -fx-background-radius:10;");
        box.setPadding(new javafx.geometry.Insets(15));
        box.setPrefSize(240, 270);
        box.setAlignment(Pos.CENTER);

        String imgPath = "/uniform_images/" + u.getCode() + ".png";
        InputStream imgIs = getClass().getResourceAsStream(imgPath);
        Image img = new Image(imgIs != null ? imgIs : getClass().getResourceAsStream("/placeholder_img.png"));
        ImageView iv = new ImageView(img);
        iv.setFitHeight(100);
        iv.setPreserveRatio(true);

        Text tName = new Text(u.getSize());
        Text tPrice = new Text("₱" + u.getPrice());

        Button btn = new Button("Order");
        btn.setPrefHeight(40);
        btn.setMaxWidth(160);
        btn.setOnAction(e -> {
            appendToTemp(u);
            btn.setText("Ordered");
            btn.setDisable(true);
            try { kiosk.showMainMenu(); } catch (Exception ex) { ex.printStackTrace(); }
        });

        box.getChildren().addAll(iv, tName, tPrice, btn);
        return box;
    }

    private void appendToTemp(Uniform u) {
        Path p = Paths.get("temp.csv");
        try (BufferedWriter w = Files.newBufferedWriter(p, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            w.write(u.getCode() + "," + u.getClassification() + " " + u.getSize() + "," + u.getPrice());
            w.newLine();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void handleBackToMainMenu() {
        try { kiosk.showMainMenu(); } catch (Exception e) { e.printStackTrace(); }
    }
}
