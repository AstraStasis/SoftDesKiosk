package com.example.softdeskiosk;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class BookController {
    @FXML
    private FlowPane productGrid;

    private Kiosk kiosk;

    public void setKiosk(Kiosk kiosk) {
        this.kiosk = kiosk;
    }

    @FXML
    public void initialize() {
        // Example product data
        addProduct("Placeholder Book 1", "₱110", "book1.png");
        addProduct("Placeholder Book 2", "₱120", "book2.png");
        addProduct("Placeholder Book 3", "₱150", "book3.png");
    }

    // Add product to the grid
    private void addProduct(String name, String price, String imagePath) {
        VBox productBox = new VBox(10);
        productBox.setPrefSize(200, 250);
        productBox.setStyle("-fx-background-color: #f9f9f9; -fx-border-radius: 10; -fx-border-width: 1; -fx-border-color: #ccc;");

        // Image
        ImageView productImage = new ImageView(new Image("file:resources/images/" + imagePath));
        productImage.setFitHeight(100);
        productImage.setFitWidth(100);
        productImage.setPreserveRatio(true);

        // Name
        Text productName = new Text(name);

        // Price
        Text productPrice = new Text(price);

        // Purchase Button
        Button purchaseButton = new Button("Purchase");
        purchaseButton.setOnAction(event -> handlePurchase(name));

        // Add all elements to the product box
        productBox.getChildren().addAll(productImage, productName, productPrice, purchaseButton);

        // Add the product box to the grid
        productGrid.getChildren().add(productBox);
    }

    // Handle purchase action (you can customize this as needed)
    private void handlePurchase(String productName) {
        System.out.println("Purchased: " + productName);
        // Add purchase logic here
    }

    // Back to Main Menu action
    @FXML
    private void handleBackToMainMenu() {
        try {
            if (kiosk != null) {
                kiosk.showMainMenu(); // Trigger transition animation (e.g., slide, fade-out)
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
