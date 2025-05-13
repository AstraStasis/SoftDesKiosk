package com.example.softdeskiosk;
import java.nio.file.Files;
import java.nio.file.Paths;

import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;


import java.io.IOException;

public class Kiosk extends Application {
    private Stage primaryStage;
    private BorderPane rootLayout;

    // ── Remember the last‐looked‐up student key ──
    private String currentStudentKey;

    public void setCurrentStudentKey(String key) {
        this.currentStudentKey = key;
    }
    public String getCurrentStudentKey() {
        return currentStudentKey;
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            Files.write(Paths.get("temp.csv"), new byte[0]); // clear file contents
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Business Affairs Office Kiosk");

        // replace with:
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Business Affairs Office Kiosk");

        rootLayout = new BorderPane();
        rootLayout.setTop(createHeader());

// get the actual usable screen bounds
        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
        double w = bounds.getWidth();
        double h = bounds.getHeight();

// create scene to fill the screen
        Scene scene = new Scene(rootLayout, w, h);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.show();


        showMainMenu();
    }

    private HBox createHeader() {
        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(20));
        header.setStyle("-fx-background-color: #2C3E50;");

        ImageView logoView;
        try {
            Image logo = new Image(getClass().getResourceAsStream("/Rizal_Technological_University.png"));
            logoView = new ImageView(logo);
            logoView.setFitHeight(60);
            logoView.setPreserveRatio(true);
        } catch (Exception e) {
            logoView = new ImageView();
            logoView.setFitHeight(60);
            logoView.setFitWidth(60);
        }

        Text title = new Text("Information and Transaction Kiosk");
        title.setStyle("-fx-fill: white; -fx-font-size: 32px; -fx-font-weight: bold;");

        header.getChildren().addAll(logoView, title);
        return header;
    }

    public void showMainMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/main_menu.fxml"));
            Parent content = loader.load();

            KioskController controller = loader.getController();
            controller.setKiosk(this);
            controller.loadCartFromCSV();
            controller.updateCartTable();

            // ── Restore student info if previously entered ──
            String key = getCurrentStudentKey();
            if (key != null && controller.hasStudent(key)) {
                controller.restoreStudentInfo(key);
            }

            animateToCenter(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showBookScene() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/book_scene.fxml"));
            Parent content = loader.load();
            BookController controller = loader.getController();
            controller.setKiosk(this);
            animateToCenter(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showUniformScene() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/uniform_scene.fxml"));
            Parent content = loader.load();
            UniformController controller = loader.getController();
            controller.setKiosk(this);
            animateToCenter(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showCheckoutScene() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/checkout.fxml"));
            Parent content = loader.load();
            CheckoutController controller = loader.getController();
            controller.setKiosk(this);
            animateToCenter(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showRegiformScene() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/regiform.fxml"));
            Parent content = loader.load();
            RegiController controller = loader.getController();
            controller.setKiosk(this);
            animateToCenter(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showGradeScene() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/grade.fxml"));
            Parent content = loader.load();
            GradeController controller = loader.getController();
            controller.setKiosk(this);
            animateToCenter(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void animateToCenter(Parent newContent) {
        Node oldContent = rootLayout.getCenter();
        if (oldContent != null) {
            TranslateTransition slideOut = new TranslateTransition(Duration.millis(200), oldContent);
            slideOut.setFromX(0);
            slideOut.setToX(-30);

            FadeTransition fadeOut = new FadeTransition(Duration.millis(200), oldContent);
            fadeOut.setFromValue(1);
            fadeOut.setToValue(0);

            slideOut.setOnFinished(ev -> {
                rootLayout.setCenter(newContent);
                newContent.setTranslateX(30);
                newContent.setOpacity(0);

                TranslateTransition slideIn = new TranslateTransition(Duration.millis(200), newContent);
                slideIn.setFromX(30);
                slideIn.setToX(0);

                FadeTransition fadeIn = new FadeTransition(Duration.millis(200), newContent);
                fadeIn.setFromValue(0);
                fadeIn.setToValue(1);

                slideIn.play();
                fadeIn.play();
            });

            slideOut.play();
            fadeOut.play();
        } else {
            rootLayout.setCenter(newContent);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
