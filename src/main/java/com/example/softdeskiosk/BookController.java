    package com.example.softdeskiosk;

    import javafx.animation.FadeTransition;
    import javafx.animation.TranslateTransition;
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

    import java.io.BufferedWriter;
    import java.io.IOException;
    import java.io.InputStream;
    import java.io.InputStreamReader;
    import java.io.BufferedReader;
    import java.net.URL;
    import java.nio.charset.StandardCharsets;
    import java.nio.file.*;
    import java.util.ArrayList;
    import java.util.LinkedHashMap;
    import java.util.List;
    import java.util.Map;


    public class BookController {
        @FXML private TextField searchBar;
        @FXML private FlowPane productGrid;
        private Map<String, List<Book>> bookMap = new LinkedHashMap<>();
        private Kiosk kiosk;

        private enum ViewState { MAIN, DEPARTMENTS, BOOKS }
        private ViewState currentView = ViewState.MAIN;

        @FXML public void initialize() {
            try (BufferedReader r = new BufferedReader(
                    new InputStreamReader(getClass().getResourceAsStream("/books.csv"),
                            StandardCharsets.UTF_8))) {
                String ln;
                while ((ln = r.readLine()) != null && !ln.isBlank()) {
                    String[] p = ln.split(",",4);
                    bookMap.computeIfAbsent(p[3], d->new ArrayList<>())
                            .add(new Book(p[0],p[1],p[2]));
                }
            } catch(IOException e){
                e.printStackTrace();
            }

            productGrid.setAlignment(Pos.CENTER);
            showDepartments();
            searchBar.textProperty().addListener((obs,o,n)->{/*…*/});
        }

        private void showDepartments() {
            currentView = ViewState.DEPARTMENTS;
            productGrid.getChildren().clear();

            for (String dept : bookMap.keySet()) {
                Button b = new Button(dept);
                b.getStyleClass().add("dept-button");
                b.setOnAction(e -> showBooks(dept));

                URL imgUrl = getClass().getResource("/dept_images/" + dept + ".png");
                if (imgUrl == null) {
                    imgUrl = getClass().getResource("/placeholder_img.png");
                }
                b.setStyle(String.join(";",
                        "-fx-background-image: url('" + imgUrl.toExternalForm() + "')",
                        "-fx-background-size: cover",
                        "-fx-background-position: center"
                ));
                FadeTransition fade = new FadeTransition(Duration.millis(500), b);
                fade.setFromValue(0);
                fade.setToValue(1);
                fade.setDelay(Duration.millis(100 * productGrid.getChildren().size()));

                TranslateTransition slide = new TranslateTransition(Duration.millis(500), b);
                slide.setFromY(20);
                slide.setToY(0);
                slide.setDelay(Duration.millis(100 * productGrid.getChildren().size()));

                fade.play();
                slide.play();
                productGrid.getChildren().add(b);

            }


        }

        private void showBooks(String dept) {
            currentView = ViewState.BOOKS;
            productGrid.getChildren().clear();

            bookMap.get(dept).forEach(book ->
                    productGrid.getChildren().add(createProductBox(book.getCode(), book.getName(), book.getPrice()))
            );
        }

        private VBox createProductBox(String id, String name, String price) {
            VBox box = new VBox(10);
            box.setStyle("-fx-border-color:#ccc; -fx-background-color:#f9f9f9; -fx-border-radius:10; -fx-background-radius:10;");
            box.setPadding(new javafx.geometry.Insets(15));
            box.setPrefSize(240, 270);
            box.setAlignment(javafx.geometry.Pos.CENTER);

            InputStream imgIs = getClass().getResourceAsStream("/book_images/" + id + ".png");
            Image img = new Image(imgIs != null ? imgIs : getClass().getResourceAsStream("/placeholder_img.png"));
            ImageView iv = new ImageView(img);
            iv.setFitHeight(100);
            iv.setPreserveRatio(true);

            Text tName = new Text(name);
            Text tPrice = new Text("₱" + price);

            Button btn = new Button("Order");
            btn.setMaxWidth(160);
            btn.setOnAction(e -> {
                try {
                    Path p = Paths.get("temp.csv");
                    try (BufferedWriter w = Files.newBufferedWriter(p, StandardCharsets.UTF_8,
                            StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
                        w.write(id + "," + name + "," + price);
                        w.newLine();
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                btn.setText("Ordered");
                btn.setDisable(true);
                try { kiosk.showMainMenu(); } catch (Exception ex) { ex.printStackTrace(); }
            });

            box.getChildren().addAll(iv, tName, tPrice, btn);
            FadeTransition fade = new FadeTransition(Duration.millis(400), box);
            fade.setFromValue(0);
            fade.setToValue(1);

            TranslateTransition slide = new TranslateTransition(Duration.millis(400), box);
            slide.setFromY(30);
            slide.setToY(0);

            fade.play();
            slide.play();

            return box;
        }

        @FXML
        private void handleBackToPrevious() {
            try {
                switch (currentView) {
                    case BOOKS -> showDepartments();
                    case DEPARTMENTS -> kiosk.showMainMenu();
                    default -> {} // at MAIN already
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void setKiosk(Kiosk kiosk) {
            this.kiosk = kiosk;
        }
    }
