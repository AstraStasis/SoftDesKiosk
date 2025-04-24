package com.example.softdeskiosk;

import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;

public class Kiosk extends Application {

    private BorderPane mainLayout;
    private VBox mainMenu;

    @Override
    public void start(Stage stage) {
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

        mainMenu = new VBox(20);
        mainMenu.setAlignment(Pos.CENTER);
        mainMenu.setPadding(new Insets(30));

        Button purchaseUniformButton = new Button("Purchase Uniform");
        Button purchaseBookButton = new Button("Purchase Book");
        Button exitButton = new Button("Exit");

        purchaseUniformButton.setOnAction(e -> showUniformScene());
        purchaseBookButton.setOnAction(e -> showBookScene());
        exitButton.setOnAction(e -> stage.close());

        mainMenu.getChildren().addAll(purchaseUniformButton, purchaseBookButton, exitButton);

        mainLayout = new BorderPane();
        mainLayout.setTop(header);
        mainLayout.setCenter(mainMenu);

        Scene scene = new Scene(mainLayout, 1920, 1080);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

        stage.setScene(scene);
        stage.setTitle("Business Affairs Office Kiosk");
        stage.show();
    }

    private void showBookScene() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(20));
        content.setAlignment(Pos.TOP_CENTER);

        TextField searchBar = new TextField();
        searchBar.setPromptText("Search books...");
        searchBar.setMaxWidth(400);

        FlowPane productGrid = new FlowPane();
        productGrid.setHgap(20);
        productGrid.setVgap(20);
        productGrid.setAlignment(Pos.CENTER);

        ArrayList<VBox> bookBoxes = new ArrayList<>();

        String[][] books = {
                {"6630", "AC-MGT102: Corporate Social Responsibility", "500.00"},
                {"6486", "BA-MGT103: Operations Management (TQM)", "375.00"},
                {"6487", "BA-MGT104: International Business and Trade", "420.00"},
                {"6489", "BA-MGT105: Strategic Management", "420.00"},
                {"6490", "BA-MGT106: Essentials of Risk Management", "350.00"},
                {"6497", "CPE01: Programming Logic & Design using Dev C++", "450.00"},
                {"6509", "ENTREP3: Opportunity Seeking To Start A Business Community", "450.00"},
                {"6523", "GEO1: Understanding the Self (Revised Edition)", "430.00"},
                {"6524", "GEO2: Readings in Philippine History", "400.00"},
                {"6525", "GEO3: The Contemporary World - NP", "410.00"},
                {"6526", "GE04: Mathematics in the Modern World (Revised edition)", "440.00"},
                {"6527", "GE05: Communicate & Connect Purposive Communication", "550.00"},
                {"6528", "GE06: An Eye for Art Appreciation: Perception and Expression", "450.00"},
                {"6529", "GE07: Science Technology and Society", "630.00"},
                {"6530", "GE08: Introduction to Ethics", "350.00"},
                {"6531", "GE09: The Life and Works of Rizal", "375.00"},
                {"6607", "GE-ELEC6: Environmental Science", "610.00"},
                {"6522", "GE-ELEC7: Entrepreneurial Mindset", "600.00"},
                {"6536", "HR105: Training and Development 2nd Edition", "400.00"},
                {"6537", "HR106: Labor Laws and Legislation 2nd Edition", "400.00"},
                {"6539", "HR110: Labor Relations and Negotiations 2nd Edition", "570.00"},
                {"6540", "HR111: Organization Development 2nd Edition", "500.00"},
                {"6631", "ITC320: Introduction to Applications Dev & Emerging Tech", "700.00"},
                {"6632", "ITP120: Essentials of Human Computer Interaction", "275.00"},
                {"6633", "ITP320: Information Assurance and Security 1", "350.00"},
                {"6611", "MM105: Pricing Strategy", "520.00"},
                {"6558", "NSTP: National Service Training Program 2", "390.00"},
                {"6560", "OM1002: Concepts and Approaches in Inventory", "640.00"},
                {"6561", "OM104: Project Management", "475.00"},
                {"6562", "OM105: Supply Chain & Logistics Management", "600.00"},
                {"6559", "OM-THESIS1: Understanding Research Design and Methods", "350.00"}
        };

        for (String[] book : books) {
            VBox productBox = createProductBox(book[1], book[2]);
            bookBoxes.add(productBox);
            productGrid.getChildren().add(productBox);
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

        ScrollPane scrollPane = new ScrollPane(productGrid);
        scrollPane.setFitToWidth(true);
        scrollPane.setPadding(new Insets(10));
        scrollPane.setStyle("-fx-background-color:transparent;");
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        Button backButton = new Button("Back to Main");
        backButton.setOnAction(e -> slideToCenter(mainMenu));

        VBox scrollableContent = new VBox(20);
        scrollableContent.setAlignment(Pos.TOP_CENTER);
        scrollableContent.getChildren().addAll(searchBar, scrollPane, backButton);

        content.getChildren().add(scrollableContent);
        slideToCenter(content);
    }

    private void showUniformScene() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(20));
        content.setAlignment(Pos.TOP_CENTER);

        TextField searchBar = new TextField();
        searchBar.setPromptText("Search uniforms...");
        searchBar.setMaxWidth(400);

        FlowPane productGrid = new FlowPane();
        productGrid.setHgap(20);
        productGrid.setVgap(20);
        productGrid.setAlignment(Pos.CENTER);

        String[][] uniforms = {
                {"U101", "PE Shirt", "355.00"},
                {"U102", "PE Pants", "450.00"},
                {"U103", "Women's Blouse", "525.00"},
                {"U104", "Women's Skirt", "425.00"},
                {"U105", "Women's Pants", "510.00"},
                {"U106", "Men's Polo", "515.00"},
                {"U107", "Men's Pants", "530.00"},
        };

        ArrayList<VBox> uniformBoxes = new ArrayList<>();
        for (String[] uniform : uniforms) {
            VBox productBox = createProductBox(uniform[1], uniform[2]);
            uniformBoxes.add(productBox);
            productGrid.getChildren().add(productBox);
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

        ScrollPane scrollPane = new ScrollPane(productGrid);
        scrollPane.setFitToWidth(true);
        scrollPane.setPadding(new Insets(10));
        scrollPane.setStyle("-fx-background-color:transparent;");
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        Button backButton = new Button("Back to Main");
        backButton.setOnAction(e -> slideToCenter(mainMenu));

        VBox scrollableContent = new VBox(20);
        scrollableContent.setAlignment(Pos.TOP_CENTER);
        scrollableContent.getChildren().addAll(searchBar, scrollPane, backButton);

        content.getChildren().add(scrollableContent);
        slideToCenter(content);
    }

    private VBox createProductBox(String nameStr, String priceStr) {
        VBox box = new VBox(10);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(15));
        box.setStyle("-fx-border-color: #ccc; -fx-border-width: 2px; -fx-background-color: #f9f9f9; -fx-border-radius: 10px; -fx-background-radius: 10px;");
        box.setPrefSize(200, 250);

        ImageView imageView = new ImageView(new Image(getClass().getResourceAsStream("/placeholder_img.png")));
        imageView.setFitHeight(120);
        imageView.setPreserveRatio(true);

        Text name = new Text(nameStr);
        Text price = new Text("₱" + priceStr);

        Button purchaseBtn = new Button("Purchase");

        box.getChildren().addAll(imageView, name, price, purchaseBtn);
        return box;
    }

    private void slideToCenter(Pane nextContent) {
        Pane currentContent = (Pane) mainLayout.getCenter();

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