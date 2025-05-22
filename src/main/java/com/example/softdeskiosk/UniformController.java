
package com.example.softdeskiosk;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.URL;
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
                    String[] parts = line.split(",", 5);
                    if (parts.length < 5) continue;

                    Uniform u = new Uniform(
                            parts[0],
                            parts[1],
                            parts[2],
                            new BigDecimal(parts[3]),
                            Integer.parseInt(parts[4])
                    );
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

    private Button createClassificationBox(String classification) {
        // 1) Create a button and style it
        Button btn = new Button(classification);
        btn.getStyleClass().add("classification-box");
        btn.setTextAlignment(TextAlignment.CENTER);

        // 2) Load the image URL (or fallback)
        URL imgUrl = getClass().getResource(
                "/uniform_images/" + classification.replaceAll("\\s+", "_") + ".png"
        );
        if (imgUrl == null) {
            imgUrl = getClass().getResource("/placeholder_img.png");
        }
        String cssUrl = imgUrl.toExternalForm();
        btn.setStyle("-fx-background-image: url(\"" + cssUrl + "\");");

        // 3) Initial state for animation
        btn.setOpacity(0);
        btn.setScaleX(0.8);
        btn.setScaleY(0.8);
        btn.setTranslateY(20);    // start 20px lower

        // 4) Build individual transitions
        ScaleTransition popIn = new ScaleTransition(Duration.millis(250), btn);
        popIn.setFromX(0.8); popIn.setFromY(0.8);
        popIn.setToX(1.0);   popIn.setToY(1.0);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(250), btn);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);

        TranslateTransition slideUp = new TranslateTransition(Duration.millis(250), btn);
        slideUp.setFromY(20);
        slideUp.setToY(0);

        // 5) Play all three in parallel
        new ParallelTransition(popIn, fadeIn, slideUp).play();

        // 6) Hover scale animation
        ScaleTransition hoverEnter = new ScaleTransition(Duration.millis(150), btn);
        hoverEnter.setToX(1.05); hoverEnter.setToY(1.05);
        ScaleTransition hoverExit = new ScaleTransition(Duration.millis(150), btn);
        hoverExit.setToX(1.0);   hoverExit.setToY(1.0);
        btn.setOnMouseEntered(e -> hoverEnter.playFromStart());
        btn.setOnMouseExited (e -> hoverExit.playFromStart());

        // 7) Click handling (transition to sizes)
        btn.setOnAction(e -> {
            FadeTransition ft = new FadeTransition(Duration.millis(300), productGrid);
            ft.setFromValue(1.0);
            ft.setToValue(0.0);
            ft.setOnFinished(evt -> showSizesFor(classification));
            ft.play();
        });

        return btn;
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
        box.setPadding(new Insets(15));
        box.setAlignment(Pos.CENTER);

        Text tSize = new Text("Size: " + u.getSize());
        tSize.setStyle("-fx-font-size: 30px; -fx-font-weight: bold;");

        Text tPrice = new Text("Price: ₱" + u.getPrice());
        tPrice.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        Text tStock = new Text(u.isInStock() ? "In stock: " + u.getStock() : "Not in stock");
        tStock.setStyle("-fx-font-size: 16px;");

        Button btn = new Button(u.isInStock() ? "Order" : "No Stocks");
        btn.setDisable(!u.isInStock());
        btn.setPrefHeight(40);
        btn.setMaxWidth(160);

        if (u.isInStock()) {
            btn.setOnAction(e -> {
                appendToTemp(u);
                u.decrementStock(); // Optional: track stock live during session
                btn.setText("Ordered");
                btn.setDisable(true);
                try { kiosk.showMainMenu(); } catch (Exception ex) { ex.printStackTrace(); }
            });
        }

        box.getChildren().addAll(tSize, tPrice, tStock, btn);
        return box;
    }

    private void appendToTemp(Uniform u) {
        Path p = Paths.get("temp.csv");
        try (BufferedWriter w = Files.newBufferedWriter(p, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND)) {
            w.write(u.getCode() + "," +
                    u.getClassification() + " " + u.getSize() + "," +
                    u.getPrice());
            w.newLine();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void handleBackToMainMenu() {
        try { kiosk.showMainMenu(); }
        catch (Exception e) { e.printStackTrace(); }
    }
}

