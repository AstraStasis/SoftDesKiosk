package com.example.softdeskiosk;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class Kiosk extends Application {
    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Kiosk System");
        showMainMenu();
    }

    public void showMainMenu() throws Exception {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/com/example/softdeskiosk/main_menu.fxml"));
        AnchorPane mainMenu = loader.load();

        KioskController controller = loader.getController();
        controller.setKiosk(this);

        Scene scene = new Scene(mainMenu);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void showBookScene() throws Exception {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/com/example/softdeskiosk/book_scene.fxml"));
        AnchorPane bookScene = loader.load();

        BookController controller = loader.getController();
        controller.setKiosk(this);

        Scene scene = new Scene(bookScene);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void showUniformScene() throws Exception {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/com/example/softdeskiosk/uniform_scene.fxml"));
        AnchorPane uniformScene = loader.load();

        UniformController controller = loader.getController();
        controller.setKiosk(this);

        Scene scene = new Scene(uniformScene);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}