package com.example.softdeskiosk;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import java.util.ArrayList;

public class BookController {
    @FXML
    private TextField searchBar;

    @FXML
    private FlowPane productGrid;

    private Kiosk kiosk;

    private final ArrayList<VBox> bookBoxes = new ArrayList<>();

    public void setKiosk(Kiosk kiosk) {
        this.kiosk = kiosk;
    }

    @FXML
    public void initialize() {
        try {
            InputStream is = getClass().getResourceAsStream("/books.csv"); // working dir: src/main/resources/books.csv
            if (is == null) {
                System.err.println("books.csv not found");
                return;
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] book = line.split(",", 3);
                if (book.length < 3) continue;
                VBox box = createProductBox(book[0], book[1], book[2]);
                productGrid.getChildren().add(box);
                bookBoxes.add(box);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        searchBar.textProperty().addListener((obs, oldVal, newVal) -> {
            productGrid.getChildren().clear();
            for (VBox box : bookBoxes) {
                Text label = (Text) box.getChildren().get(1);
                if (label.getText().toLowerCase().contains(newVal.toLowerCase())) {
                    productGrid.getChildren().add(box);
                }
            }
        });
    }

    private VBox createProductBox(String idStr, String nameStr, String priceStr) {
        VBox box = new VBox(10);
        box.setStyle("-fx-border-color: #ccc; -fx-border-width: 2px; -fx-background-color: #f9f9f9; -fx-border-radius: 10px; -fx-background-radius: 10px;");
        box.setPadding(new javafx.geometry.Insets(15));
        box.setPrefSize(240, 270);
        box.setAlignment(javafx.geometry.Pos.CENTER);

        Image image;
        InputStream imageStream = getClass().getResourceAsStream("/book_images/" + idStr + ".png");
        if (imageStream != null) {
            image = new Image(imageStream);
        } else {
            image = new Image(getClass().getResourceAsStream("/placeholder_img.png"));
        }

        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(100);
        imageView.setPreserveRatio(true);

        Text name = new Text(nameStr);
        Text price = new Text("₱" + priceStr);

        Button purchaseBtn = new Button("Order");
        purchaseBtn.setMaxWidth(160);
        purchaseBtn.setOnAction(e -> addToCart(idStr, nameStr, priceStr));

        box.getChildren().addAll(imageView, name, price, purchaseBtn);
        return box;
    }

    private void addToCart(String id, String name, String price) {
        try {
            java.nio.file.Path path = java.nio.file.Paths.get("temp.csv");
            java.nio.file.Files.write(
                    path,
                    (id + "," + name + "," + price + System.lineSeparator()).getBytes(java.nio.charset.StandardCharsets.UTF_8),
                    java.nio.file.StandardOpenOption.CREATE,
                    java.nio.file.StandardOpenOption.APPEND
            );
        } catch (Exception ex) {
            ex.printStackTrace();
        }
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
