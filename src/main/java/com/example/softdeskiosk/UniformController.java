package com.example.softdeskiosk;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.util.ArrayList;

public class UniformController {
    @FXML
    private TextField searchBar;

    @FXML
    private FlowPane productGrid;

    private Kiosk kiosk;
    private final ArrayList<VBox> uniformBoxes = new ArrayList<>();

    public void setKiosk(Kiosk kiosk) {
        this.kiosk = kiosk;
    }

    @FXML
    public void initialize() {
        String[][] uniforms = {
                {"U101", "PE Shirt", "355.00"},
                {"U102", "PE Pants", "450.00"},
                {"U103", "Women's Blouse", "525.00"},
                {"U104", "Women's Skirt", "425.00"},
                {"U105", "Women's Pants", "510.00"},
                {"U106", "Men's Polo", "515.00"},
                {"U107", "Men's Pants", "530.00"},
        };

        for (String[] uniform : uniforms) {
            VBox box = createProductBox(uniform[1], uniform[2]);
            productGrid.getChildren().add(box);
            uniformBoxes.add(box);
        }

        searchBar.textProperty().addListener((obs, oldVal, newVal) -> {
            productGrid.getChildren().clear();
            for (VBox box : uniformBoxes) {
                Text label = (Text) box.getChildren().get(1);
                if (label.getText().toLowerCase().contains(newVal.toLowerCase())) {
                    productGrid.getChildren().add(box);
                }
            }
        });
    }

    private VBox createProductBox(String nameStr, String priceStr) {
        VBox box = new VBox(10);
        box.setStyle("-fx-border-color: #ccc; -fx-border-width: 2px; -fx-background-color: #f9f9f9; -fx-border-radius: 10px; -fx-background-radius: 10px;");
        box.setPadding(new javafx.geometry.Insets(15));
        box.setPrefSize(240, 270);
        box.setAlignment(javafx.geometry.Pos.CENTER);

        ImageView imageView = new ImageView(new Image(getClass().getResourceAsStream("/placeholder_img.png")));
        imageView.setFitHeight(100);
        imageView.setPreserveRatio(true);

        Text name = new Text(nameStr);
        Text price = new Text("₱" + priceStr);

        Button purchaseBtn = new Button("Order");
        purchaseBtn.setMaxWidth(160);

        box.getChildren().addAll(imageView, name, price, purchaseBtn);
        return box;
    }

    @FXML
    private void handleBackToMainMenu() {
        try {
            if (kiosk != null) {
                kiosk.showMainMenu();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
