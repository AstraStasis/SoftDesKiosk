package com.example.softdeskiosk;

import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

public class Kiosk extends Application {
    private Stage primaryStage;
    private BorderPane rootLayout; // holds header (top) and sliding content (center)

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Business Affairs Office Kiosk");

        // Create layout
        rootLayout = new BorderPane();
        rootLayout.setTop(createHeader());

        Scene scene = new Scene(rootLayout, 1920, 1080);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        primaryStage.setScene(scene);
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
        return header;
    }

    public void showMainMenu() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/main_menu.fxml"));
        Parent content = loader.load();
        KioskController controller = loader.getController();
        controller.setKiosk(this);

        animateToCenter(content);
    }

    public void showBookScene() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/book_scene.fxml"));
        Parent content = loader.load();
        BookController controller = loader.getController();
        controller.setKiosk(this);

        animateToCenter(content);
    }

    public void showUniformScene() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/uniform_scene.fxml"));
        Parent content = loader.load();
        UniformController controller = loader.getController();
        controller.setKiosk(this);

        animateToCenter(content);
    }

    private void animateToCenter(Parent newContent) {
        // Optional: Fade and slide old content out
        Parent oldContent = (Parent) rootLayout.getCenter();

        if (oldContent != null) {
            TranslateTransition slideOut = new TranslateTransition(Duration.millis(200), oldContent);
            slideOut.setFromX(0);
            slideOut.setToX(-30);
            slideOut.setInterpolator(javafx.animation.Interpolator.EASE_BOTH);

            FadeTransition fadeOut = new FadeTransition(Duration.millis(200), oldContent);
            fadeOut.setFromValue(1);
            fadeOut.setToValue(0);

            slideOut.setOnFinished(e -> {
                rootLayout.setCenter(newContent);

                newContent.setTranslateX(30);
                newContent.setOpacity(0);

                TranslateTransition slideIn = new TranslateTransition(Duration.millis(200), newContent);
                slideIn.setFromX(30);
                slideIn.setToX(0);
                slideIn.setInterpolator(javafx.animation.Interpolator.EASE_BOTH);

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
