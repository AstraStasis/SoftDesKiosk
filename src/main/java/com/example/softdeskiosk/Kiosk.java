package com.example.softdeskiosk;

import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Kiosk extends Application {

    private BorderPane mainLayout;
    private VBox mainMenu;

    @Override
    public void start(Stage stage) {
        // --- HEADER ---
        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(20));
        header.setStyle("-fx-background-color: #2C3E50;");

        ImageView logoView;
        try {
            Image logo = new Image(getClass().getResourceAsStream("/logo_bao_.png"));
            logoView = new ImageView(logo);
            logoView.setFitHeight(60);
            logoView.setPreserveRatio(true);
        } catch (Exception e) {
            logoView = new ImageView();
            logoView.setFitHeight(60);
            logoView.setFitWidth(60);
        }

        Text title = new Text("Business Affairs Office Kiosk");
        title.setStyle("-fx-fill: white; -fx-font-size: 32px; -fx-font-weight: bold;");
        header.getChildren().addAll(logoView, title);

        // --- MAIN MENU ---
        mainMenu = new VBox(20);
        mainMenu.setAlignment(Pos.CENTER);
        mainMenu.setPadding(new Insets(30));

        Button purchaseUniformButton = new Button("Purchase Uniform");
        Button purchaseBookButton = new Button("Purchase Book");
        Button exitButton = new Button("Exit");

        purchaseUniformButton.setOnAction(e -> showProductScene("Uniform", 3));
        purchaseBookButton.setOnAction(e -> showProductScene("Book", 10));
        exitButton.setOnAction(e -> stage.close());

        mainMenu.getChildren().addAll(purchaseUniformButton, purchaseBookButton, exitButton);

        // --- MAIN LAYOUT ---
        mainLayout = new BorderPane();
        mainLayout.setTop(header);
        mainLayout.setCenter(mainMenu);

        Scene scene = new Scene(mainLayout, 1920, 1080);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

        stage.setScene(scene);
        stage.setTitle("Business Affairs Office Kiosk");
        stage.show();
    }

    private void showProductScene(String type, int count) {
        VBox content = new VBox(20);
        content.setPadding(new Insets(20));
        content.setAlignment(Pos.TOP_CENTER);

        FlowPane productGrid = new FlowPane();
        productGrid.setHgap(20);
        productGrid.setVgap(20);
        productGrid.setAlignment(Pos.CENTER);

        for (int i = 1; i <= count; i++) {
            VBox productBox = new VBox(10);
            productBox.setAlignment(Pos.CENTER);
            productBox.setPadding(new Insets(15));
            productBox.setStyle("-fx-border-color: #ccc; -fx-border-width: 2px; -fx-background-color: #f9f9f9; -fx-border-radius: 10px; -fx-background-radius: 10px;");
            productBox.setPrefSize(200, 250);

            // Placeholder image
            ImageView imageView;
            try {
                Image placeholder = new Image(getClass().getResourceAsStream("/placeholder_img.png"));
                imageView = new ImageView(placeholder);
            } catch (Exception e) {
                imageView = new ImageView(); // fallback if image is missing
            }
            imageView.setFitHeight(120);
            imageView.setPreserveRatio(true);

            Text name = new Text("Placeholder " + type + " " + i);
            Text price = new Text("₱" + (100 + i * 10));
            Button purchaseBtn = new Button("Purchase");

            productBox.getChildren().addAll(imageView, name, price, purchaseBtn);
            productGrid.getChildren().add(productBox);
        }

        Button backButton = new Button("Back to Main");
        backButton.setOnAction(e -> slideToCenter(mainMenu));

        content.getChildren().addAll(productGrid, backButton);

        slideToCenter(content);
    }

    private void slideToCenter(Pane nextContent) {
        Pane currentContent = (Pane) mainLayout.getCenter();

        // Slide + Fade Out
        TranslateTransition slideOut = new TranslateTransition(Duration.millis(300), currentContent);
        slideOut.setFromX(0);
        slideOut.setToX(-50);
        slideOut.setInterpolator(javafx.animation.Interpolator.EASE_IN);

        FadeTransition fadeOut = new FadeTransition(Duration.millis(300), currentContent);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);

        slideOut.setOnFinished(event -> {
            mainLayout.setCenter(nextContent);

            nextContent.setTranslateX(50);
            nextContent.setOpacity(0.0);

            // Slide + Fade In
            TranslateTransition slideIn = new TranslateTransition(Duration.millis(300), nextContent);
            slideIn.setFromX(50);
            slideIn.setToX(0);
            slideIn.setInterpolator(javafx.animation.Interpolator.EASE_OUT);

            FadeTransition fadeIn = new FadeTransition(Duration.millis(300), nextContent);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);

            slideIn.play();
            fadeIn.play();
        });

        slideOut.play();
        fadeOut.play();
    }

    public static void main(String[] args) {
        launch();
    }
}
